package com.example.teachapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    List<Team> list_items;
    Handler handler =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = revHandler;
        sendRequestWithHttpClient();

        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListView", "onItemClick: "+ position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final AlertDialog dialog = builder.create();
                final View dialogView = View.inflate(MainActivity.this, R.layout.score_window, null);
                //设置对话框布局
                TextView viewName = dialogView.findViewById(R.id.textView_alert_name);
                viewName.setText(list_items.get(position).getName());
                EditText editScore = dialogView.findViewById(R.id.editText_alert_score);
                editScore.setText(list_items.get(position).getScore()+"");
                dialog.setView(dialogView);
                dialog.show();

                Button btnLogin = (Button) dialogView.findViewById(R.id.btn_login);
                Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView viewName = dialogView.findViewById(R.id.textView_alert_name);
                        EditText editScore = dialogView.findViewById(R.id.editText_alert_score);
                        final String name = viewName.getText().toString();
                        final String score = editScore.getText().toString();
                        if (TextUtils.isEmpty(score)) {
                            Toast.makeText(getApplicationContext(), "分数", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Socket socket = new Socket("182.92.114.73", 8899);
                                    OutputStream outputStream = socket.getOutputStream();
                                    JSONObject json = new JSONObject();
                                    json.put("type", "setscore");
                                    json.put("team", name);
                                    json.put("score", Integer.parseInt(score));
                                    outputStream.write(json.toString().getBytes());
                                    socket.close();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    Handler revHandler = new Handler() {

        //handleMessage为处理消息的方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<Team> tmplist = (List<Team>)msg.obj;
            for(int i=0;i<tmplist.size();i++){
                Log.d("obj", tmplist.get(i).getName());
                Log.d("obj","" +tmplist.get(i).getImageId());
                Log.d("obj", ""+tmplist.get(i).getScore());
            }
            TeamAdapter adapter = new TeamAdapter(MainActivity.this,R.layout.team_itms,(List<Team>)msg.obj);
            listView.setAdapter(adapter);
        }
    };

    private void sendRequestWithHttpClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {

//                HttpURLConnection conn = null;
                while (true) {
                    try {


                        Socket socket = new Socket("182.92.114.73", 8899);
                        OutputStream outputStream = socket.getOutputStream();
                        JSONObject json = new JSONObject();
                        json.put("type", "getteams");
                        Log.d(this.toString(), "send: "+json.toString());
                        outputStream.write(json.toString().getBytes());
                        InputStream inputStream = socket.getInputStream();
                        byte[] buffer = new byte[2048];
                        int len = inputStream.read(buffer);
                        String rev = new String(buffer,0,len);
                        Log.d(this.toString(), "rev: "+rev);
                        JSONObject json_re = new JSONObject(rev);
                        String type = json_re.getString("type");
                        if(type.equals("teams_info"))
                        {
                            JSONArray jsonArray = json_re.getJSONArray("plist");
                            list_items = new ArrayList<Team>();
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject job = jsonArray.getJSONObject(i);
                                String j_name = job.getString("name");
                                int j_score = job.getInt("score");
                                Team team_ = new Team(j_name,R.drawable.point,j_score);
                                list_items.add(team_);
                            }
                            Message msg = Message.obtain();
                            msg.obj = list_items;

                            //msg.obj = new String(buffer,0,len);
                            handler.sendMessage(msg);
//                            TeamAdapter adapter = new TeamAdapter(MainActivity.this,R.layout.team_itms,list_items);
//                            listView.setAdapter(adapter);
                        }
                        socket.close();
                        Thread.sleep(25000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();
    }
}
