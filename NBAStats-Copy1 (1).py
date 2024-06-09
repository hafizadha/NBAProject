#!/usr/bin/env python
# coding: utf-8

# In[235]:


from selenium import webdriver
from selenium.webdriver.support.ui import Select
from bs4 import BeautifulSoup
import pandas as pd


#1st website to scrape Player Bios
driver = webdriver.Chrome()
url = 'https://www.espn.com/nba/players'
driver.get(url)

src = driver.page_source
soup = BeautifulSoup(src,"html.parser")
linktable = soup.find("div",attrs = {"class":"span-4"})
links = linktable.findAll('a') 
linklist = [href.get("href") for href in links[0:]] #Gives all link in the website (links are usually in 'a' tags)
filteredlink = [ele for ele in linklist if ele.startswith("https://www.espn.com/nba/")] #Extract wanted links

for i in range(len(filteredlink)):
    filteredlink[i] = filteredlink[i].replace("/team/_/", "/team/roster/_/") 
#Navigate to the Roster Section of Team's page URL to get a specific table
#Since each team's page (each in different URL) has the same pattern in URL, it's easier to replace them.

playerbios = []

for link in filteredlink:
    url1 = link;
    driver.get(url1)
    src = driver.page_source
    soup2 = BeautifulSoup(src,"html.parser")
    
    firstname = soup2.find("span",attrs = {"class":"db pr3 nowrap"}).contents[0]
    secondname = soup2.find("span",attrs = {"class":"db fw-bold"}).contents[0]
    teamname = firstname + " " + secondname
    
    
    #Parsing the bio table in this table
    biotable = parser.find("div",attrs = {"class":"Table__Scroller"})
    bios = biotable.findAll('div',attrs = {"class":"inline"})
    biolist = [h.getText().split()for h in bios[0:]]
    
    biolist1 = []
    currentdata = []

    for entry in biolist:
        if entry:
            currentdata.extend(entry)
        else:
            if currentdata:
                biolist1.append(currentdata)
                currentdata = []

    # If the last set of data is not added due to no breaker
    if currentdata:
        biolist1.append(currentdata)
        
    
    for data in biolist1:
        indexindicator = 0

        if "Jr." in data[2] or "IV" in data[2] or "II" in data[2] or "Sr." in data[2]:
            name = data[0] + " " + data[1] + " "+ ''.join(filter(lambda x: not x.isdigit(), data[2]))
            indexindicator += 1
        else:
            name =  data[0] + " " + ''.join(filter(lambda x: not x.isdigit(), data[1]))



        feet_str = data[4+indexindicator].replace("'", '')
        inches_str = data[5+indexindicator].replace('"', '')

        # Convert feet and inches to integers
        feet = int(feet_str)
        inches = float(inches_str)


        # Convert height to metrics (cm)
        height_cm = int((feet * 12 + inches) * 2.54)


        # Convert weight to kg
        weight = round(float(int(data[6+indexindicator]) * 0.453592),1)
        
        
        UpCase = [letter for letter in teamname if letter.isupper()] #Getting uppercase letters as codename
        codename = ''.join(UpCase)
        
        
        # Name,
        new_data = [name,teamname,codename, data[2+indexindicator], data[3+indexindicator], height_cm, weight,data[-1]]

        playerbios.append(new_data)

biodf = pd.DataFrame(playerbios, columns=["Name","Team","TeamCodename","POS","Age","Height(cm)","Weight(kg)","Salary"])


# 2nd Website to scrape Player Stats
driver1 = webdriver.Chrome()
driver1.get("https://www.nba.com/stats/players/traditional")

#Functions to select a value in a specific dropdown
select1 = Select(driver1.find_element("xpath",r'/html/body/div[1]/div[2]/div[2]/div[3]/section[2]/div/div[2]/div[2]/div[1]/div[3]/div/label/div/select'))
select1.select_by_index(0) #In the website, the dropdown selected is 'All' (Shows all players)

src1 = driver1.page_source
soup1 = BeautifulSoup(src1,"html.parser")
statstable1 = soup1.find("tbody",attrs = {"class":"Crom_body__UYOcU"})
rows1 = statstable1.findAll('tr') #Find all table rows of this table
statslist1 = [[[td.getText().split() for td in rows1[i].findAll('td')]] for i in range(len(rows1))] #Getting contents( td tags ) inside each row (tr tag)

#Create new list consistings of only wanted stats
new_statslist = []
for row in statslist1:
    
    name = ' '.join(row[0][1])
    
    points = row[0][8][0]
    
    oreb = row[0][18][0]
    dreb = row[0][19][0]
    reb = row[0][20][0]
    ast = row[0][21][0]
    tov = row[0][22][0]
    stl = row[0][23][0]
    block = row[0][24][0]
    
    extracteddata = [name,points,oreb,dreb,reb,ast,tov,stl,block] #Filtered from the original list ( A bit hardcoded :3 )
    new_statslist.append(extracteddata) #Add them into the new list

statsdf = pd.DataFrame(new_statslist, columns=["Name","Points","Offensive Rebound","Defensive Rebound","Rebound","Assist","Turnover","Steal","Block"]) #Create a Pandas Dataframe for Player Stats

merged_df = pd.merge(biodf, statsdf, on='Name', how='inner') #Merge player bio and stats dataset into one, with the player's name as references to match row.
#Players that don't exists in other dataset are removed automatically

test1 = merged_df #Creating a testing dataset to maintain purity of original

# Need to convert current Salary in which its values are in the form of string (because of "$","," and "--") into int type, so can standardize later on
test1['Salary'] = test1['Salary'].str.replace('$', '',regex=False).str.replace(',', '',regex=False)
test1['Salary'] = test1['Salary'].str.replace('--', '0',regex=False)
test1['Salary'] = test1['Salary'].astype(int)


test1['PlayerID'] = range(1, len(test1) + 1)

#Standardize salary of each player to range of 1000-3500
salaries = test1['Salary'].to_list()

#Values for Standardization formula
Xmin = 0
newrange = 3500-1000
oldrange = test1['Salary'].max() - 0
newmin = 1000
index = 0

for asalary in salaries:
    standardsalary = int((asalary - Xmin) * (newrange/oldrange) + newmin)#Using standardization formula
    
    standardsalary = int(standardsalary/100) * 100 #Make last two digits into zeros
    
    salaries[index] = standardsalary #Replace into new value
    index += 1

#Replacing all values in the column 7 which is Salary
test1.iloc[0:,7] = salaries


#3rd website to scrape Team Infos
driver2 = webdriver.Chrome()
driver2.get("https://en.wikipedia.org/wiki/National_Basketball_Association")

src = driver2.page_source
soup2 = BeautifulSoup(src,"html.parser")
teamtable = soup2.find("table",attrs = {"class":"wikitable sortable jquery-tablesorter"})
tablebody = teamtable.find("tbody")

teamrow = tablebody.findAll('tr')
teaminfo = [[td.getText().split() for td in teamrow[i].findAll(['td'])] for i in range(len(teamrow))]

selectedteam = ['Spurs','Warriors','Celtics','Heat','Lakers','Suns','Magic','Nuggets','Thunder','Rockets']
newteaminfo = []
for row in teaminfo:
    
    teamexists = False
    teamname = ''
    if row[0][1] in selectedteam or len(row[0]) == 3:
        if(len(row[0]) == 3):
            if row[0][2] in selectedteam:
                teamexists = True
        
        else:
            teamexists = True
    
    
    if teamexists:
        teamname = ' '.join(row[0])
        
        UpCase = [letter for letter in teamname if letter.isupper()] #Getting uppercase letters as codename
        codename = ''.join(UpCase)
        location = ' '.join(row[1])
        Arena = ' '.join(row[2])
        
        newdata = [teamname,codename,location,Arena]
        
        newteaminfo.append(newdata)

teamdf = pd.DataFrame(newteaminfo,columns=['Team Name','Codename','Location','Arena'])
teamdf.xs(6)['Location'] = "Los Angeles, California"
teamdf.xs(6)['Arena'] = "Crypto.com Arena"

playerjson = test1.to_json(r'C:\\Users\Hafiz\\Downloads\\PlayerJSON.json',orient='index')
teamjson = teamdf.to_json(r'C:\\Users\\Hafiz\\Downloads\\TeamJSON.json',orient='index')

