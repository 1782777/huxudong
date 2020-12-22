import socketserver
import json
from abc import ABCMeta, abstractmethod
import struct
import time
import threading
#import numpy as np

CARLIST=[]
PHONELIST=[]
TEAMLIST =[]
MSGLIST = []

class MyTCPHandler(socketserver.BaseRequestHandler):
    

    def handle(self):
        mtype = ''
        name = ''
        # try:
        #     #data = self.request.recv(4)
        #     data = self.request.recv(1024).decode('utf8')
        #     self.request.sendall(data.upper().encode())
        #     print(data)
        #     json_info = json.loads(data)
        #     if json_info['type'] =='updategps':
        #         team_name =json_info['id']
        #         lon = json_info['lon']
        #         lat = json_info['lat']
        #         hasteam = False
        #         for t in TEAMLIST:
        #             if t.name == team_name:
        #                 t.update(lon,lat)
        #                 hasteam = True
        #         if not hasteam :
        #             team = Team(team_name)

        # except:
        #     print ('hack attack me!')
        
        data = self.request.recv(1024).decode('utf8')
        # self.request.sendall(data.upper().encode())
        print(data)
        json_info = json.loads(data)
        if json_info['type'] =='updategps':
            team_name =json_info['id']
            lon = json_info['lon']
            lat = json_info['lat']
            
            hasteam = False
            for t in TEAMLIST:
                if t.name == team_name:
                    t.update(lon,lat)
                    hasteam = True
            if not hasteam :
                team = Team(team_name)
                TEAMLIST.append(team)

            for t in TEAMLIST:
                if t.name == team_name:
                    msg_dic ={'type':'score','score':t.score}
                    msg_js = json.dumps(msg_dic)
                    self.request.sendall(msg_js.encode('utf-8'))
        if json_info['type'] =='getteams':
            self.get_teamlist()
        if json_info['type'] =='setscore':
            t_name = json_info['team']
            score = json_info['score']
            for t in TEAMLIST:
                if t_name == t.name:
                    t.score = score
        if json_info['type'] == 'sendmsg':
            s_name = json_info['s_name']
            r_name = json_info['r_name']
            msg_type = json_info['msg_type']
            msg = json_info['msg']
            msgs =  Msg(s_name,r_name,msg_type,msg)
            MSGLIST.append(msgs)
        if json_info['type'] == 'getmsg':
            s_name = json_info['s_name']
            for ms_ in MSGLIST:
                if ms_.hearman == s_name:
                    msg_dic ={'type':'msg','s_name':ms_.sayman,'msgtype':msg_type,'msgs':ms_.msg}
                    msg_js = json.dumps(msg_dic)
                    self.request.sendall(msg_js.encode('utf-8'))
        
    def setup(self):
        print("connet ：",self.client_address)
        
    def finish(self):
        print("disconnet")
        # for car in CARLIST:
        #     if car.add == self.client_address:
        #         CARLIST.remove(car)

    def get_teamlist(self):
        if len(TEAMLIST) ==0:
            return
        teamList = []
        for t in TEAMLIST:
            teaminfo ={'name':t.name,'lon':t.lon,'lat':t.lat,'score':t.score}
            teamList.append(teaminfo)
        
        msg_dic ={'type':'teams_info'}
        msg_dic.update({'plist':teamList})
        msg_js = json.dumps(msg_dic)
        self.request.sendall(msg_js.encode('utf-8'))

    
        
class Team():
    def __init__(self,name): 
        self.name = name
        self.lon = 1
        self.lat = 1
        self.score = 0
        self.live = True
        self.lastheart  =0
        self.heart = 0
        self.thread = threading.Thread(target=self.loop)
        self.thread.setDaemon(True)
        self.thread.start()
        print ("init a team")
        # self.loop()

    def update(self,lon,lat):
        self.heart = self.heart +1
        self.lon = lon
        self.lat = lat
        print (len(TEAMLIST))
        
    def loop(self):
        while True:
            time.sleep(15)
            if self.lastheart == self.heart:
                # TEAMLIST.remove(self)
                # del(self)
                # print(len(TEAMLIST))
                self.live = False
                
                # break
            else:
                self.live = True
                self.lastheart =self.heart

class Msg():
    def __init__(self,sayman,hearman,msgtype,msg):
        self.sayman = sayman
        self.hearman = hearman
        self.msgtype = msgtype
        self.msg = msg


class Manager():
    __metaclass__ = ABCMeta
    def __init__(self,resquest,address):
        self.request =resquest
        self.add = address
        self.link = None
        self.buff_len=0
        self.loop()
    
    def loop(self):
        while True:
            try:
                data=self.request.recv(1024)
                #print(data)
            except Exception as e:
                print(self.add,"disconnect",e)
                self.request.close()
                

            if not data:
                print("connection lost")
                break
            #print(self.add,data)
            self.rev(data)

    @abstractmethod
    def rev(self,data):
        pass 

    def send_data(self,data):
        # self.request.send(data)  
        
        try:
            ret =struct.pack('i',len(data))
            self.request.send(ret)
            self.request.send(data)
            #print ret,data
        except:
            #self.disconnect()
            print ('phone send fald')

    def sendcarlist(self):
        print(len(CARLIST))
        dic_list =[]
        for cars in CARLIST:
            dic_name ={'name':cars.add}
            dic_list.append(dic_name)
        dic = {'type':'carlist'}
        dic.update({'carlist':dic_list})
        js = json.dumps(dic)
        print (js)
        self.send_data(js.encode())
           

class Car(Manager):
    def __init__(self,resquest,address):
        print('join a car')
        CARLIST.append(self)
        super(Car, self).__init__(resquest,address)

    def rev(self,data):
        if len(data)==4:
            self.buff_len = struct.unpack('i',data)[0]
            print(self.buff_len)
            if self.link:
                ret =struct.pack('i',self.buff_len)
                self.link.request.send(ret)

        tmplen = 0
        
        while tmplen<self.buff_len:
            try:
                data=self.request.recv(self.buff_len)
                if self.link:
                    self.link.request.send(data)
                tmplen+=len(data)
            except Exception as e:
                print(self.add,"disconnect",e)
                self.request.close()
            if not data:
                print("connection lost")
       
       
    


class Phone(Manager):
    def __init__(self,resquest,address):
        print('join a phone')
        PHONELIST.append(self)
        super(Phone, self).__init__(resquest,address)
        
    def rev(self,data):
        
        try:
            data_json= data.decode('utf8')
            json_info = json.loads(data_json)
            if json_info['type']=='getcarlist':
                self.sendcarlist()
            if json_info['type']=='linkcar':
                self.linkcar(json_info['ip'])
        except Exception as e:
            print ('phonerev;',e)
        if self.link:
            self.link.request.send(data)
       
    def linkcar(self,ip):
        for car in CARLIST:
            print (ip ,car.add)
            if ip == car.add:
                self.link = car
                car.link = self
                # dic = {'type':'phonelink'}
                # js = json.dumps(dic)
                # self.link.send(js.encode())
                print ('link a car~')
            

# HOST,PORT = "172.16.41.225",8899
HOST,PORT = "172.17.253.202",8899

server=socketserver.ThreadingTCPServer((HOST,PORT),MyTCPHandler)#mutilt
server.serve_forever()
