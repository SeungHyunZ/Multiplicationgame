package com.darkbluesharp.multiplicationgame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.currentTimeMillis;

public class MainActivity extends AppCompatActivity {

    private AdView mAdview, mAdview2; //애드뷰 변수 선언

    private TimerTask second;
    private TextView timer_text, timer_text2;
    private final Handler handler = new Handler();

    private int timer_sec = 0; //제한시간
    private int count = 0;  //점수
    private int highCount = 0;
    private int tempHighCount = 0;
    private int level = 1;  //초기 레벨
    private int period = 200;  //시간 줄어드는 주기
    private int correctCount = 0;
    private int wrongCount = 0;

    private long time ;

    private int bungi = 0;


    TextView multipl, multipl2, multipl3, multipl4, left, right, score, highscore, levelTextView, up;

    private int firstInt, secondInt, leftInt, rightInt;

    Boolean gameStatus = false;

    // 가중치(layout_weight) 동적으로 설정
    LinearLayout.LayoutParams params, params2;

    Dialog finishdialog01, resultdialog01; // 커스텀 다이얼로그

    private MediaPlayer background;
    //Sound
    SoundPool soundPool;	//작성
    int changeSound, correctSound, incorrectSound, levelupSound, endingSound;
    int criticalSound1, criticalSound2, criticalSound3, criticalSound4;

    private Boolean soundOnOff = true;  //true 는 현재 사운드 설정상태 On
    private Boolean soundStatus = false;  //false 는 현재 배경음악 나오고 있지 않는 상태태
    private Boolean backgroundStatus = false; //true 는 background상태

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //타이틀바 없애기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화 //
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
      /*  mAdview = findViewById(R.id.adView); //배너광고 레이아웃 가져오기
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);*/


        timer_text = findViewById(R.id.timer);
        timer_text2 = findViewById(R.id.timer2);
        multipl = findViewById(R.id.multipl);
        multipl2 = findViewById(R.id.multipl2);
        multipl3 = findViewById(R.id.multipl3);
        multipl4 = findViewById(R.id.multipl4);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        score = findViewById(R.id.score);
        highscore = findViewById(R.id.highscore);
        levelTextView = findViewById(R.id.levelTextView);
        up = findViewById(R.id.up);
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.right);

      //  timer_text.setText("****************************************");

        //timerStart();

        // 가중치(layout_weight) 동적으로 설정
       params = (LinearLayout.LayoutParams) timer_text.getLayoutParams();
       params2 = (LinearLayout.LayoutParams) timer_text2.getLayoutParams();

       //저장된 최고점수 불러오기
        SharedPreferences pref = getSharedPreferences("multiplication", MODE_PRIVATE);
        highCount  = pref.getInt("highCount", 0);
        highscore.setText(highCount+"");

        finishdialog01 = new Dialog(MainActivity.this);       // Dialog 초기화
        finishdialog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        finishdialog01.setContentView(R.layout.finishdialog01);

        resultdialog01 = new Dialog(MainActivity.this);
        resultdialog01.requestWindowFeature(Window.FEATURE_NO_TITLE);
        resultdialog01.setContentView(R.layout.resultdialog01);

        mAdview = finishdialog01.findViewById(R.id.adView); //배너광고 레이아웃 가져오기
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

        mAdview2 = resultdialog01.findViewById(R.id.adView2); //배너광고 레이아웃 가져오기
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdview2.loadAd(adRequest2);



        Button.OnClickListener onClickListener = new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.left :

                        confirmMultipl("left");
                        break ;
                    case R.id.right :

                        confirmMultipl("right");

                        break ;
                    case R.id.multipl :
                        if(soundOnOff&&!backgroundStatus){
                            soundPool.play(changeSound,1,1,0,0,1);
                        }

                        int temp = bungi%10;

                        if(temp==0) {
                            multipl.setBackgroundResource(android.R.color.darker_gray);
                            multipl.setTextColor(Color.parseColor("#000000"));
                        }else if(temp==1){
                            multipl.setBackgroundResource(android.R.color.holo_red_light);
                            multipl.setTextColor(Color.parseColor("#FFFFFFFF"));
                        }else if(temp==2){
                            multipl.setBackgroundResource(R.color.purple_500);
                            multipl.setTextColor(Color.parseColor("#CC0000"));
                        }else if(temp==3){
                            multipl.setBackgroundResource(android.R.color.holo_green_light);
                            multipl.setTextColor(Color.parseColor("#FFBB33"));
                        }else if(temp==4){
                            multipl.setBackgroundResource(R.color.teal_700);
                            multipl.setTextColor(Color.parseColor("#FF4444"));
                        }else if(temp==5){
                            multipl.setBackgroundResource(R.color.black);
                            multipl.setTextColor(Color.parseColor("#FFFFFF"));
                        }else if(temp==6){
                            multipl.setBackgroundResource(android.R.color.darker_gray);
                            multipl.setTextColor(Color.parseColor("#CC0000"));
                        }else if(temp==7){
                            multipl.setBackgroundResource(android.R.color.holo_red_light);
                            multipl.setTextColor(Color.parseColor("#AAAAAA"));
                        }else if(temp==8){
                            multipl.setBackgroundResource(android.R.color.white);
                            multipl.setTextColor(Color.parseColor("#FF4444"));
                        }else if(temp==9){
                            multipl.setBackgroundResource(android.R.color.holo_orange_dark);
                            multipl.setTextColor(Color.parseColor("#0099CC"));
                        }

                        bungi++;

                        break ;
                    case R.id.timer:
                    case R.id.timer2:
                    case R.id.levelTextView:

                        if(level>10){
                            break;
                        }
                        if(soundOnOff&&!backgroundStatus){
                            soundPool.play(levelupSound,1,1,0,0,1);
                        }

                        level++;
                        count = count + 60;

                        levelTextView.setText("level "+level);
                        score.setText(count + "");

                        up.setVisibility(View.VISIBLE);
                        timer_text.setVisibility(View.GONE);
                        timer_text2.setVisibility(View.GONE);
                        levelTextView.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //딜레이 후 시작할 코드 작성
                                up.setVisibility(View.GONE);
                                timer_text.setVisibility(View.VISIBLE);
                                timer_text2.setVisibility(View.VISIBLE);
                                levelTextView.setVisibility(View.VISIBLE);
                            }
                        }, 100
                        );// 0.1초 정도 딜레이를 준 후 시작

                        break ;


                }
            }
        } ;

        left.setOnClickListener(onClickListener) ;
        right.setOnClickListener(onClickListener) ;
        timer_text.setOnClickListener(onClickListener) ;
        timer_text2.setOnClickListener(onClickListener) ;
        levelTextView.setOnClickListener(onClickListener) ;
        multipl.setOnClickListener(onClickListener);


        setMultipl();

        //음악설정
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);	//작성
        changeSound = soundPool.load(this,R.raw.changescore,1);
        correctSound = soundPool.load(this,R.raw.correct,1);
        incorrectSound = soundPool.load(this,R.raw.incorrect,1);
        criticalSound1 = soundPool.load(this,R.raw.excellent,1);
        criticalSound2 = soundPool.load(this,R.raw.fantastic,1);
        criticalSound3 = soundPool.load(this,R.raw.nice,1);
        criticalSound4 = soundPool.load(this,R.raw.wow,1);
        levelupSound = soundPool.load(this,R.raw.levelup,1);
        endingSound = soundPool.load(this,R.raw.ending,1);

        background = MediaPlayer.create(getBaseContext(), R.raw.backgroundsound);
        background.setLooping(true);  //반복설정
      //  background.start();



      //  soundPool.play(backgroundSound,1,1,0,0,1);

       // soundPool.stop(backgroundSound);
    }

    @Override
    protected void onPause() {
        background.pause();
        backgroundStatus = true;
        super.onPause();
    }


    @Override
    protected void onResume(){
        if(soundStatus&&soundOnOff){
            background.start();
        }
        backgroundStatus = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        background.release();
        super.onDestroy();
    }

    public void timerStart() {
        timer_text = (TextView) findViewById(R.id.timer);
        timer_sec = 500;
        //count = 0;
        //level = 1;
        tempHighCount = highCount;
        correctCount = 0;
        wrongCount = 0;
        second = new TimerTask() { //
            @Override
            public void run() {

                if(timer_sec<=0){
                    if (second != null)
                        second.cancel();
                    gameStatus = false;
                    //   Toast.makeText(MainActivity.this, "게임 종료", Toast.LENGTH_SHORT).show();
                }

            //    Log.e("Test", "Timer start");
                Update();
                count ++;
                timer_sec = timer_sec - level;



            }
        };
        Timer timer = new Timer();
        timer.schedule(second, 0, period);
    }
    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {

                if(!gameStatus){
                   // timer_text.setText("****************************************");
                  //  levelTextView.setText("level 1");


                    levelTextView.setText("level "+level);
                    score.setText(count + "");
                    String resultText = "";
                    if(tempHighCount>highCount){
                        highCount = tempHighCount;
                        SharedPreferences pref = getSharedPreferences("multiplication", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("highCount", highCount);
                        editor.commit();
                        resultText = resultText + "Congraturation!\nNew Record\n";
                    }
                    params.weight = 500; //에너지바
                    timer_text.setLayoutParams(params);

                    params2.weight = 500; //에너지바 오른쪽 공백
                    timer_text2.setLayoutParams(params2);

                    resultText = resultText +
                            "level "+level+
                            "\nSCORE "+count+
                            "\ncorrect answer "+correctCount+
                            "\nwrong answer "+wrongCount;
                    showResultDialo(resultText);

                    count = 0;
                    level = 1;

                }else {

                    if(timer_sec>1000){
                        timer_sec = 1000;
                    }

                    //에너지바 세팅
                    params.weight = timer_sec; //에너지바
                    timer_text.setLayoutParams(params);

                    params2.weight = 1000-timer_sec; //에너지바 오른쪽 공백
                    timer_text2.setLayoutParams(params2);

                  /*  String timerString = "";
                    for (int i = 0; i < timer_sec; i++) {
                        timerString += "*";
                    }*/

                    if(count>level*85){
                        level++;
                        if(soundOnOff&&!backgroundStatus){
                            soundPool.play(levelupSound,1,1,0,0,1);
                        }

                        up.setVisibility(View.VISIBLE);
                        timer_text.setVisibility(View.GONE);
                        timer_text2.setVisibility(View.GONE);
                        levelTextView.setVisibility(View.GONE);

                        new Handler().postDelayed(new Runnable()
                                                  {
                                                      @Override
                                                      public void run()
                                                      {
                                                          //딜레이 후 시작할 코드 작성
                                                          up.setVisibility(View.GONE);
                                                          timer_text.setVisibility(View.VISIBLE);
                                                          timer_text2.setVisibility(View.VISIBLE);
                                                          levelTextView.setVisibility(View.VISIBLE);
                                                      }
                                                  }, 200
                        );// 0.1초 정도 딜레이를 준 후 시작

                    }

                    if(tempHighCount<count){
                        tempHighCount= count;
                        highscore.setText(count+"");
                    }

                    score.setText(count + "");
                   // timer_text.setText(timerString);
                    levelTextView.setText("level "+level);
                }
            }
        };
        handler.post(updater);
    }

    public void setMultipl(){
        firstInt = nansu();
        secondInt = nansu();

        if(nansu()>5){
            leftInt = firstInt*secondInt;
            rightInt = Integer.parseInt(nansu()+""+nansu());
            if(nansu()==1){ //특별한 오답
                rightInt = leftInt+1;
            }
            if(nansu()==2){ //특별한 오답
                rightInt = leftInt-1;
            }

        }else{
            rightInt = firstInt*secondInt;
            leftInt = Integer.parseInt(nansu()+""+nansu());
            if(nansu()==1){ //특별한 오답
                leftInt = rightInt+1;
            }
            if(nansu()==2){ //특별한 오답
                leftInt = rightInt-1;
            }
        }

        multipl.setText(firstInt+" X "+secondInt);
        left.setText(leftInt+"");
        right.setText(rightInt+"");

        time = currentTimeMillis();

    }

    public void confirmMultipl(String input){

       int tempNansu = nansu();

       if(!soundStatus&&soundOnOff){
           background.start();
           soundStatus = true;
       }

        long responsTime = currentTimeMillis() - time; //정답까지 걸린시간
        long criticalTime = 1100 ; //크리티컬 기준 시간
        long showResultTime = 150 ; //결과를 보여줄 시간

        if(!gameStatus){
            timerStart();
            gameStatus = true;
        }

        if(input.equals("left")){
            if(leftInt==firstInt*secondInt){ //정답
                timer_sec = timer_sec +40  ;
                count +=2;
                correctCount ++;

                if(responsTime<criticalTime) { //정답을 빨리 선택했다면
                    if(soundOnOff&&!backgroundStatus){
                        if(tempNansu==1||tempNansu==2||tempNansu==3) {
                            soundPool.play(criticalSound1, 1, 1, 0, 0, 1);
                        }else if(tempNansu==4||tempNansu==5){
                            soundPool.play(criticalSound2, 1, 1, 0, 0, 1);
                        }else if(tempNansu==6||tempNansu==7){
                            soundPool.play(criticalSound3, 1, 1, 0, 0, 1);
                        }else if(tempNansu==8||tempNansu==9){
                            soundPool.play(criticalSound4, 1, 1, 0, 0, 1);
                        }
                    }
                    multipl.setVisibility(View.GONE);
                    multipl2.setVisibility(View.VISIBLE);
                    timer_sec = timer_sec +20  ;
                }else{
                    if(soundOnOff&&!backgroundStatus){
                        soundPool.play(correctSound,1,1,0,0,1);
                    }
                    multipl.setVisibility(View.GONE);
                    multipl3.setVisibility(View.VISIBLE);
                }

            }else{ //오답
                if(soundOnOff&&!backgroundStatus){
                    soundPool.play(incorrectSound,1,1,0,0,20);
                }

                timer_sec -=70;
                wrongCount ++;

                multipl.setVisibility(View.GONE);
                multipl4.setVisibility(View.VISIBLE);

            }
        }else{
            if(rightInt==firstInt*secondInt){ //정답
                timer_sec = timer_sec + 40  ;
                count +=2;
                correctCount ++;

                if(responsTime<criticalTime) { //정답을 빨리 선택했다면
                    if(soundOnOff&&!backgroundStatus){
                        if(tempNansu==1||tempNansu==2||tempNansu==3) {
                            soundPool.play(criticalSound1, 1, 1, 0, 0, 1);
                        }else if(tempNansu==4||tempNansu==5){
                            soundPool.play(criticalSound2, 1, 1, 0, 0, 1);
                        }else if(tempNansu==6||tempNansu==7){
                            soundPool.play(criticalSound3, 1, 1, 0, 0, 1);
                        }else if(tempNansu==8||tempNansu==9){
                            soundPool.play(criticalSound4, 1, 1, 0, 0, 1);
                        }
                    }
                    multipl.setVisibility(View.GONE);
                    multipl2.setVisibility(View.VISIBLE);
                    timer_sec = timer_sec +20  ;
                }else{
                    if(soundOnOff&&!backgroundStatus){
                        soundPool.play(correctSound,1,1,0,0,1);
                    }
                    multipl.setVisibility(View.GONE);
                    multipl3.setVisibility(View.VISIBLE);
                }

            }else{ //오답
                if(soundOnOff&&!backgroundStatus){
                    soundPool.play(incorrectSound,1,1,0,0,20);
                }
                timer_sec -=70;
                wrongCount ++;

                multipl.setVisibility(View.GONE);
                multipl4.setVisibility(View.VISIBLE);

            }
        }

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //딜레이 후 시작할 코드 작성
                multipl.setVisibility(View.VISIBLE);
                multipl2.setVisibility(View.GONE);
                multipl3.setVisibility(View.GONE);
                multipl4.setVisibility(View.GONE);
            }
        }, showResultTime);// 0.1초 정도 딜레이를 준 후 시작


        setMultipl();
        Update();

    }

    public int nansu(){
        double dValue = Math.random();
        int idValue =   (int)(dValue * 10);

        if(idValue==0){
            return 7;
        }else{
            return idValue;
        }
    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
        showFinishDialog01();
    }

    // dialog01을 디자인하는 함수
    public void showFinishDialog01(){


        finishdialog01.show(); // 다이얼로그 띄우기

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로~
        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

        // 아니오 버튼
        TextView noBtn = finishdialog01.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                finishdialog01.dismiss(); // 다이얼로그 닫기
            }
        });
        // 네 버튼
        finishdialog01.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                finish();           // 앱 종료
            }
        });

      //  finishdialog01.show(); // 다이얼로그 띄우기

        //showResultDialo

    }

    // dialog01을 디자인하는 함수
    public void showResultDialo(String answer){

        background.pause();
        soundStatus = false;

        if(soundOnOff&&!backgroundStatus){
            soundPool.play(endingSound,1,1,0,0,1);
        }


        resultdialog01.setCancelable(false);
        resultdialog01.show(); // 다이얼로그 띄우기

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로~
        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        TextView gameoverText;
        gameoverText = resultdialog01.findViewById(R.id.gameoverText);
        gameoverText.setText(answer);

        // 네 버튼
        resultdialog01.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                resultdialog01.dismiss(); // 다이얼로그 닫기
            }
        });

        //  finishdialog01.show(); // 다이얼로그 띄우기

        //showResultDialo

    }

}