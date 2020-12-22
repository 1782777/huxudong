using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.IO;


using System.Net.Sockets;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Resources;
using System.Net;
using System.Threading;
using System.ComponentModel;

namespace WpfApp1
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        BindingList<Team> TEAMLIST = new BindingList<Team>();
        public MainWindow()
        {
            InitializeComponent();
            //string advertResourcePath = Directory.GetCurrentDirectory() + @"\Resources";
            webbrowser.Source = new Uri(Environment.CurrentDirectory + @"\Resources\baidu.html");
            listView1.ItemsSource = TEAMLIST;
            Thread thread = new Thread(TcpLoop);
            thread.Start();

           
            //for (int i = 0; i < 32; i++) {
            //    Team team = new Team("zx");
            //    team.score = 0;
            //    listView1.Items.Add(team);
            //}
            
        }

        public delegate void DeleFunc();
        public void Func()
        {
            //要调用的UI元素  
            //webbrowser.InvokeScript("clean");
            Console.WriteLine("UI!!!!!!!!");
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            TextBox tb = this.FindName("TextBox1") as TextBox;
            string advertResourcePath = Directory.GetCurrentDirectory() + @"\Resources";
            Array arrayAdvertResourcePath = Directory.GetFiles(advertResourcePath);
            foreach (string advertPath in arrayAdvertResourcePath)
            {
                Console.WriteLine(advertPath);
                tb.Text = advertPath;
            }

            //string path = System.Environment.CurrentDirectory;
            //tb.Text = path;
        }

        private void Item_Click(object sender, RoutedEventArgs ee)
        {
            var btn = sender as Button;

            var t = btn.DataContext as Team;
            TalkWin tw = new TalkWin(t);
            tw.Show();

        }


        private void TcpLoop() {
            Thread.Sleep(5000);
            while (true)
            {
                
                

                TcpClient client = new TcpClient("182.92.114.73", 8899);
                NetworkStream sendStream = client.GetStream();
                string jsonText = @"{""type"" : ""getteams""}";
                Byte[] sendBytes = Encoding.Default.GetBytes(jsonText);
                sendStream.Write(sendBytes, 0, sendBytes.Length);

                NetworkStream ns = client.GetStream();
                byte[] bytes = new byte[2048];
                int bytesread = ns.Read(bytes, 0, bytes.Length);
                string msg = Encoding.Default.GetString(bytes, 0, bytesread);
                Console.WriteLine(msg);
                JObject studentsJson = JObject.Parse(msg);

                string type = studentsJson["type"].ToString();

                JArray array2 = JArray.Parse(studentsJson["plist"].ToString());

                Dispatcher.Invoke(
                       new Action(
                           delegate
                           {
                               webbrowser.InvokeScript("clean");
                              
                           }
                       ));

                for (int i = 0; i < array2.Count; i++)
                {
                    Console.WriteLine(array2[i]["name"] + ":" + array2[i]["lon"].ToString() + ":" + array2[i]["lat"].ToString() + ":" + array2[i]["score"].ToString());
                    Dispatcher.Invoke(
                        new Action(
                            delegate
                            {
                                //出问题的代码块
                                try
                                {
                                    
                                    //listView1.Items.Add(t);
                                    Team team = new Team(array2[i]["name"].ToString(), int.Parse(array2[i]["score"].ToString()));
                                    double[] res = getBaiducoor(new double[] { double.Parse(array2[i]["lon"].ToString()), double.Parse(array2[i]["lat"].ToString()) });
                                    team.lon = res[0];
                                    team.lat = res[1];

                                    bool has = false;
                                    for (int j = 0;j<TEAMLIST.Count;j++) {
                                        if (TEAMLIST[j].teamName.Equals(team.teamName))
                                        {
                                            has = true;
                                            TEAMLIST[j] = team;
                                           
                                        }
                                    }
                                    if (!has) {
                                        TEAMLIST.Add(team);
                                    }
                                    

                                    
                                    webbrowser.InvokeScript("update_make", new object[] { team.lon, team.lat, "得分：" + team.score.ToString(), "队伍：" + team.teamName });
                                }
                                catch (Exception) {
                                }
                            }
                        ));
                }
                //foreach (JObject j in array2)
                //{
                //    bool has = false;
                //    foreach (Team t in TEAMLIST)
                //    {
                //        if (t.teamName.Equals(j["name"].ToString()))
                //        {
                //            has = true;
                //            //update
                //            try
                //            {
                //                double[] res = getBaiducoor(new double[] { double.Parse(j["lon"].ToString()), double.Parse(j["lat"].ToString()) });
                //                t.lon = res[0];
                //                t.lat = res[1];
                //                Console.WriteLine(t.lon + ":" + t.lat);
                //                t.score = int.Parse(j["score"].ToString());
                //                continue;
                //            }
                //            catch (Exception) { 

                //            }
                            
                //        }
                //    }
                //    if (!has)
                //    {
                //        Dispatcher.Invoke(
                //        new Action(
                //            delegate
                //            {
                //                //出问题的代码块

                //                //listView1.Items.Add(t);
                //                Team team = new Team(j["name"].ToString(),j["score"]);
                                
                //                TEAMLIST.Add(team);
                //            }
                //        ));
                        
                //    }

                //}

                //Dispatcher.Invoke(
                //        new Action(
                //            delegate
                //            {
                //                //出问题的代码块
                //                webbrowser.InvokeScript("clean");
                //                //listView1.Items.Clear();
                //            }
                //    ));

                
                //foreach (Team t in TEAMLIST)
                //{
                //    Console.WriteLine((float)t.lon);
                //    Dispatcher.Invoke(
                //        new Action(
                //            delegate
                //            {
                //                //出问题的代码块
                                
                //                //listView1.Items.Add(t);
                //                webbrowser.InvokeScript("update_make", new object[] { t.lon, t.lat, "得分：" + t.score.ToString(), "队伍：" + t.teamName });
                //            }
                //    ));
                    

                //}


                sendStream.Flush();
                sendStream.Close();
                Thread.Sleep(1000);
                
            }
            
        }

        public static double[] getBaiducoor(double[] coord)//坐标转换的方法
        {
            double longitude = coord[0];
            double latitude = coord[1];
            Console.WriteLine(longitude);
            Console.WriteLine(latitude);
            //需要转的gps经纬度
            string convertUrl = "http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + longitude + "&y=" + latitude + "";

            HttpWebRequest request = (HttpWebRequest)System.Net.HttpWebRequest.Create(convertUrl);//创建http请求
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            Stream responseStream = response.GetResponseStream();
            StreamReader sr = new StreamReader(responseStream, Encoding.GetEncoding("utf-8"));
            string responseTxt = sr.ReadToEnd();
            sr.Close();
            sr.Dispose();

            CoorConvert mapconvert = new CoorConvert();//创建存放结果的对象
            mapconvert = JsonConvert.DeserializeObject<CoorConvert>(responseTxt);//赋值

            string lon = mapconvert.x;
            string lat = mapconvert.y;

            byte[] xBuffer = Convert.FromBase64String(lon);//坐标base64解密
            string strX = Encoding.UTF8.GetString(xBuffer, 0, xBuffer.Length);

            byte[] yBuffer = Convert.FromBase64String(lat);
            string strY = Encoding.UTF8.GetString(yBuffer, 0, xBuffer.Length);

            double[] coor = new double[2];
            coor[0] = Convert.ToDouble(strX);
            coor[1] = Convert.ToDouble(strY);
            return coor;
        }

        public class CoorConvert
        {
            public string error { get; set; }
            public string x { get; set; }
            public string y { get; set; }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {

        }
    }

    public class Team : INotifyPropertyChanged{

        //public string teamName;
        //public int score;
        public double lon, lat;

        public event PropertyChangedEventHandler PropertyChanged;
       
        public void OnPropertyChanged(PropertyChangedEventArgs e)
        {
            if (PropertyChanged != null)
            {
                PropertyChanged(this, e);
            }
        }
        public string teamName { get; set; }
        public int score { get; set; }
        public Team(string name,int score)
        {
            Console.WriteLine("init a team" + name);
            this.teamName = name;
            this.score = score;
            this.lon = 0.0;
            this.lat = 0.0;
        }
    }
}
