package tiredcoder.com.htmlunittest;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    EditText email;
    EditText password;
    CheckBox check,quiz;
    Button button;
    TextView subjectname;
    Context context;
    int automode;
    TextView count;
    Toolbar toolbar;
    String x;
    WebClient client;
    int i;
    int j;
    ArrayList<String> quizlinks=new ArrayList<>();
    CheckBox rememberme;
    protected  void onCreate(Bundle savedInstancestate)
    {
        context=this;
        super.onCreate(savedInstancestate);
        setContentView(R.layout.login);
        email=findViewById(R.id.email);
        button=findViewById(R.id.btn_login);
        password=findViewById(R.id.password);
        quiz=findViewById(R.id.quiz);

        x=getIntent().getStringExtra("returning");
        check=findViewById(R.id.check);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(Login.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);            // Permission is not granted
    }
        SharedPreferences sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        if(x==null) {
            email.setText(sharedPreferences.getString("email", null));
            password.setText(sharedPreferences.getString("password", null));
        }
        rememberme=findViewById(R.id.remember);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ema=email.getText().toString();
                String pas=password.getText().toString();
                if(validate())
                { Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    if(check.isChecked())
                        automode=1;
                    else
                        automode=0;
                    if(rememberme.isChecked())
                    {
                        SharedPreferences sharedPreferences=getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("email",ema);
                        editor.putString("password",pas);
                        editor.apply();
                    }

                    startAutomode(ema,pas); }
            }
        });
    }

       public boolean validate() {
        boolean valid = true;

        String e = email.getText().toString();
        String p = password.getText().toString();

        if (e.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.setError("Enter a valid Email Address");
            email.requestFocus();

            valid = false;
        } else {
            email.setError(null);
        }

        if (p.isEmpty() || password.length() < 4 || password.length() > 20) {
            password.setError("between 4 and 20 alphanumeric characters");
            password.requestFocus();
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }
void startAutomode(String email,String password)
{
    quizlinks.clear();
    Dialog dialog=new Dialog(this);
    dialog.setContentView(R.layout.dialoglayout);
    dialog.setCancelable(false);
    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 500);
    // dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.show();
    subjectname=dialog.findViewById(R.id.subject);
    int quizchecked;
    if(quiz.isChecked())
        quizchecked=1;
    else
        quizchecked=0;
    count=dialog.findViewById(R.id.count);
    ProgressBar progressBar=dialog.findViewById(R.id.my_progressBar);
    new Thread(new Runnable() {
        @Override
        public void run() {
            client = new WebClient();
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            client.addWebWindowListener(new WebWindowListener() {
                @Override
                public void webWindowOpened(WebWindowEvent event) {

                }

                @Override
                public void webWindowContentChanged(WebWindowEvent event) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String nUrl = event.getNewPage().getUrl().toString();

                            Log.i("urls",event.getNewPage().getUrl().toString());
                        }
                    });
                }

                @Override
                public void webWindowClosed(WebWindowEvent event) {

                }
            });
            try {
                HtmlPage page = client.getPage("http://mydy.dypatil.edu/");
                HtmlInput query=page.getElementByName("username");
                query.setValueAttribute(email);
                HtmlSubmitInput submitInput=page.getElementByName("next");
                page=   submitInput.click();
                query=page.getElementByName("password");
                query.setValueAttribute(password);
                submitInput=(HtmlSubmitInput)page.getElementById("loginbtn");
                page=submitInput.click();
                if(!page.getUrl().toString().equals("http://mydy.dypatil.edu/rait/my/"))
                {runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Login.this,"Wrong email or password",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });
                }

                page=client.getPage("http://mydy.dypatil.edu/rait/blocks/academic_status/ajax.php?action=myclasses");
                Document d=Jsoup.parse( page.getWebResponse().getContentAsString());
                Elements table=d.select("table[class=generaltable]").select("tbody").select("tr");


             //  for(int i=0;i<table.size();i++)
               for(int i=0;i<table.size();i++)
                {
                    progressBar.setProgress(0);

                    Elements td=table.eq(i).select("td");
                    String link=td.select("a").attr("href");
                    String name=td.eq(0).text();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            subjectname.setText(name);
                        }
                    });
                    //   String templink=anchor.get(i).getHrefAttribute();
                    int index=link.indexOf("?id=");
                    int id=Integer.parseInt(link.substring(index+4,link.length()));

                    page=client.getPage("http://mydy.dypatil.edu/rait/course/customview.php?id="+id);
                    org.jsoup.nodes.Document mainpage =Jsoup.parse(page.getWebResponse().getContentAsString());
                    Elements activityinstance=mainpage.select("a[class=pending]");
                    if(quizchecked==1) {
                        Elements forquiz = mainpage.select("a[class=completed]");
                        for (int j = 0; j < forquiz.size(); j++)
                            if (forquiz.get(j).attr("href").contains("quiz"))
                                quizlinks.add(forquiz.get(j).attr("href"));
                    }
                    for ( j=0;j<activityinstance.size();j++) {
                        page=client.getPage(activityinstance.get(j).attr("href"));
                        if(activityinstance.get(j).attr("href").contains("forum")&&check.isChecked() && page.getByXPath("//*[@id=\"newdiscussionform\"]/div/input[2]").size()>0)
                        {    HtmlSubmitInput button= (HtmlSubmitInput) page.getByXPath("//*[@id=\"newdiscussionform\"]/div/input[2]").get(0);

                page=button.click();
                query=page.getElementByName("subject");
                query.setValueAttribute(".");
               // query=page.getElementByName("message[text]");
                HtmlTextArea textArea= (HtmlTextArea) page.getElementByName("message[text]");
                textArea.setText(".");
                query.setValueAttribute(".");
                HtmlSubmitInput submitform= (HtmlSubmitInput) page.getElementByName("submitbutton");
                submitform.click(); }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                count.setText(j+"/"+activityinstance.size());
                                progressBar.setProgress((100 *j)/activityinstance.size());

                            }
                        });
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(100);
                            Toast.makeText(Login.this,name+" completed",Toast.LENGTH_SHORT).show();

                        }
                    });
                    //    HtmlAnchor link=(HtmlAnchor)page.getByXPath("//div[@class='content']/a").get(0);
                    //    Log.i("anchors",link.getHrefAttribute());
                }

               if(quizchecked==1) {
                   for ( i = 0; i < quizlinks.size(); i++) {
                       Log.i("quizurls",quizlinks.get(i));
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               subjectname.setText("Quiz");
                               count.setText(i+"/"+quizlinks.size());
                               progressBar.setProgress((100 *i)/quizlinks.size());

                           }
                       });
                       page = client.getPage(quizlinks.get(i));
                       if (page.getForms().get(0).getInputsByValue("Re-attempt quiz").size() > 0) {
                           Log.i("loggingpatanhikya", page.getForms().get(0).getInputsByName("Re-attempt quiz").toString());
                       } else {
                           try {
                               HtmlSubmitInput attempt = (HtmlSubmitInput) page.getForms().get(0).getInputByValue("Attempt quiz now");
                               page = attempt.click();
                               String url = page.getUrl().toString();

                               int in = url.lastIndexOf("=");
                               String attemptid = url.substring(in + 1, url.length());
                               page = client.getPage("http://mydy.dypatil.edu/rait/mod/quiz/summary.php?attempt=" + attemptid);
                               int sizeofforms = page.getForms().size();
                               if (sizeofforms == 1)
                                   in = 0;
                               else
                                   in = sizeofforms - 1;
                               HtmlSubmitInput submitattempt = page.getForms().get(in).getInputByValue("Submit all and finish");
                               page = submitattempt.click();
                               Log.i("loggingurl", attemptid);
                           }
                           catch(Exception e)
                           {

                               continue;
                           }

                       }

                   }
                   }
                   page=client.getPage("http://mydy.dypatil.edu/rait/login/logout.php");
               try {
                   HtmlSubmitInput logout = page.getForms().get(0).getInputByValue("Continue");
                   logout.click();
               }catch (Exception e)
               {
                   dialog.dismiss();
               }
                dialog.dismiss();
            } catch (Throwable t) {
                dialog.dismiss();
                Log.e("HU", "Failed to load page", t);
                runOnUiThread(() -> Toast.makeText(Login.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        }
    }).start();
}
}
