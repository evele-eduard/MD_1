from html import entities
from re import I
import requests, time
from datetime import datetime
url = 'https://api.telegram.org/bot1487584465:AAEu9R8SCt4TfPRNfiuQKg3c3eiCUlof21I'
lastUpdateID = 248032507


while True:
    response = requests.get(url + '/getUpdates').json()
    if not response['ok']:
        print('error')
    else:
        updates = response['result']
        for update in updates:
            #print(update)
            if int(update['update_id'] > lastUpdateID):
                print(update['message']['text'] + " " + " "+ datetime.fromtimestamp(update['message']['date']).strftime('%d-%m-%Y %H:%M:%S'))
            if update['message']['chat']['id'] != 441555837:
                requests.get(url + '/sendMessage?chat_id={}&text={}'.format(update['message']['chat']['id'], '"' + update['message']['text'] + '" - Жак Фреско' ))
            lastUpdateID = int(update['update_id'])
    
   time.sleep(1)

input()