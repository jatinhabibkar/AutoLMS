package tiredcoder.com.htmlunittest;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import net.sourceforge.htmlunit.corejs.javascript.tools.shell.Main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
public class MainActivity extends AppCompatActivity {
    WebClient client;
    int j;
String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Page refreshed!", Snackbar.LENGTH_LONG)
//                        .show();
//            }
//        });
        email=getIntent().getStringExtra("email");
        password=getIntent().getStringExtra("password");
       Dialog dialog=new Dialog(this);
       dialog.setContentView(R.layout.dialoglayout);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, 500);
       // dialog.getWindow().setGravity(Gravity.BOTTOM);
       dialog.show();
        ProgressBar progressBar=dialog.findViewById(R.id.my_progressBar);
        TextView mainText = findViewById(R.id.main_text);

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
                       //     mainText.append(
                         //              Html.fromHtml("<br><font color='red'><b>&nbsp;&nbsp;&nbsp;&nbsp;"
                       //                        + nUrl
                   //                         + "</b></font><br>"));
                    //           mainText.append(String.valueOf(event.getNewPage().getWebResponse().getContentAsString()).trim());
                                mainText.append(event.getNewPage().getUrl().toString()+"\n");
                     //    mainText.append(event.getNewPage().getUrl().toString());
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
                                Toast.makeText(MainActivity.this,"Wrong email or password",Toast.LENGTH_SHORT).show();

                            }
                        });
                            finish();
                        }

                    page=client.getPage("http://mydy.dypatil.edu/rait/blocks/academic_status/ajax.php?action=myclasses");
                    Document d=Jsoup.parse( page.getWebResponse().getContentAsString());
                    Elements table=d.select("table[class=generaltable]").select("tbody").select("tr");

                  //  for(int i=0;i<anchor.size();i++)
                  //  {
                  //      Log.i("anchors",anchor.get(i).getHrefAttribute());
                  //  }
                    for(int i=0;i<table.size();i++)
                    {
                        progressBar.setProgress(0);

                        Elements td=table.eq(i).select("td");
                        String link=td.select("a").attr("href");
                        String name=td.eq(0).text();
                     //   String templink=anchor.get(i).getHrefAttribute();
                        int index=link.indexOf("?id=");
                        int id=Integer.parseInt(link.substring(index+4,link.length()));

                        page=client.getPage("http://mydy.dypatil.edu/rait/course/customview.php?id="+id);
                        org.jsoup.nodes.Document mainpage =Jsoup.parse(page.getWebResponse().getContentAsString());
                        Elements activityinstance=mainpage.select("a[class=pending]");

                        for ( j=0;j<activityinstance.size();j++) {
                        page=client.getPage(activityinstance.get(j).attr("href"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress((100)/activityinstance.size()*j);

                                }
                            });
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(100);
                                Toast.makeText(MainActivity.this,name+" completed",Toast.LENGTH_SHORT).show();

                            }
                        });
                        //    HtmlAnchor link=(HtmlAnchor)page.getByXPath("//div[@class='content']/a").get(0);
                    //    Log.i("anchors",link.getHrefAttribute());
                    }
                    page=client.getPage("http://mydy.dypatil.edu/rait/mod/forum/view.php?id=329972");
                    HtmlButton button= (HtmlButton) page.getByXPath("//*[@id=\"newdiscussionform\"]/div/input[2]").get(0);
                    page=button.click();
                    Log.d("loggingurl",page.getUrl().toString());
                    dialog.dismiss();
                } catch (Throwable t) {
                    Log.e("HU", "Failed to load page", t);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show());
                }
            }
        }).start();
    }
    @Override
    public void onBackPressed() {
            finishAffinity();  //closes application
    }
}
