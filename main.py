import requests, time
from datetime import datetime
url = 'https://api.telegram.org/bot1487584465:AAEu9R8SCt4TfPRNfiuQKg3c3eiCUlof21I'


f = open("D:\MD_1\lastupdate.txt", "r")
lastUpdateID = int((f.read()))
f.close()
while True:

    response = requests.get(url + '/getUpdates').json()
    if not response['ok']:
        print('error')
    else:
        updates = response['result']
        print(lastUpdateID)
        for update in updates:
            if int(update['update_id'] > lastUpdateID):
                
                if 'message' in update:
                    print(update['message']['text'] + " " + " "+ datetime.fromtimestamp(update['message']['date']).strftime('%d-%m-%Y %H:%M:%S'))
                    #if update['message']['chat']['id'] != 441555837:
                    text = '"Голубое братство" - надёжная платформа гей-знакомств. Для начала введите свой город:'
                    #requests.get(url + '/sendMessage?chat_id={}&text={}'.format(update['message']['chat']['id'], '"' + text))
                lastUpdateID += 1
                f = open("D:\MD_1\lastupdate.txt", "w")
                f.write(str(lastUpdateID))
                f.close()
    
    time.sleep(1)

input()