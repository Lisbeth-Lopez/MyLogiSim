package com.lisbethlopez.logisim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.RelativeLayout;
//The Grid class in charge of creating the grid for the game and initializing the block properties of the grid.
class Grid{
    private int numberHorizontalPixels;
    private int numberVerticalPixels;
    private int blockSize;
    private final int gridWidth = 30;
    private int gridHeight;
    private Canvas myCanvas;

    Grid(Point size, Canvas myCanvas){
        numberHorizontalPixels=size.x;
        numberVerticalPixels=size.y;
        blockSize=numberHorizontalPixels/gridWidth;
        gridHeight=numberVerticalPixels/blockSize;
        this.myCanvas=myCanvas;

    }
    void drawGridLines(Paint paint){
        paint.setColor(Color.argb(255, 0, 0, 0));
        myCanvas.drawColor(Color.argb(255, 255, 255, 255));
        for(int i=0;i<gridWidth; i++){
            myCanvas.drawLine(blockSize*i,0,blockSize*i,numberVerticalPixels,paint);
        }
        for(int i=0;i<gridHeight; i++){
            myCanvas.drawLine(0,blockSize*i,numberHorizontalPixels,blockSize*i,paint);
        }
    }
    int getBlockSize(){return blockSize;}
}
//-----------------------------------------------------
class Screen  {
    private float horizontalTouched = -100;
    private float verticalTouched = -100;
    private float startX;
    private float startY;
    private float endX;
    private float endY;

// converts screen coordinates to grid coordinates
    void coordinateConversion(float touchX, float touchY, int blockSize) {
        horizontalTouched = (int) touchX / blockSize;
        verticalTouched = (int) touchY / blockSize;
        endX = touchX;
        endY = touchY;
    }
    void gatePosition(float x, float y){
        this.startX = x;
        this.startY = y;
    }
    void drawLines(Canvas myCanvas, Paint paint){
        myCanvas.drawLine(startX,startY,endX,endY,paint);
    }

    float getHorizontalTouched() { return horizontalTouched; }
    float getVerticalTouched() { return verticalTouched; }
}
//code from professor not implemented yet with the rest of the code.
interface Node{boolean eval();}
class Switch implements Node {
    private boolean state;
    Switch(boolean state) {this.state = state;}
    private void toggle() {this.state = !this.state;}
    public boolean eval() {return state;}
}
class NOT implements Node {
    private Node n;
    NOT(){}
    public NOT(Node n) {this.setSource(n);}
    private void setSource(Node n) {this.n = n;}
    public boolean eval() {return !n.eval();}
}
class OR implements Node {
    private Node a;
    private Node b;
    OR() {}
    public OR(Node a, Node b) { this.setA(a);this.setB(b); }
    private void setA(Node n) { this.a = n; }
    private void setB(Node n) { this.b = n; }
    public boolean eval() { return a.eval() | b.eval(); }
}
class AND implements Node {
    private Node a;
    private Node b;
    AND() {}
    public AND(Node a, Node b) { this.setA(a);this.setB(b); }
    private void setA(Node n) { this.a = n; }
    private void setB(Node n) { this.b = n; }
    public boolean eval() { return a.eval() & b.eval(); }
}
public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;
    Grid grid;
    AND and;
    OR or;
    NOT not;
    Switch aSwitch;
    Button andGate;
    Button orGate;
    Button notGate;
    Button led;
    ToggleButton toggleSwitch;
    Button delete;
    Button run;
    Button wire;
    Button save;
    ViewGroup mainLayout;
    Screen screen;
    int xDelta;
    int yDelta;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the current device's screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main);

        //set up buttons
        andGate = findViewById(R.id.and);
        orGate = findViewById(R.id.or);
        notGate = findViewById(R.id.not);
        toggleSwitch = findViewById(R.id.toggle);
        led = findViewById(R.id.light);
        delete = findViewById(R.id.delete);
        run = findViewById(R.id.run);
        wire = findViewById(R.id.wire);
        save = findViewById(R.id.save);
        andGate.setOnClickListener(this);
        orGate.setOnClickListener(this);
        notGate.setOnClickListener(this);
        toggleSwitch.setOnClickListener(this);
        led.setOnClickListener(this);
        delete.setOnClickListener(this);
        run.setOnClickListener(this);
        wire.setOnClickListener(this);
        save.setOnClickListener(this);

        //set up screen
        blankBitmap = Bitmap.createBitmap(size.x,size.y,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        gameView = findViewById(R.id.background);
        paint = new Paint();
        screen = new Screen();
        grid = new Grid(size,canvas);
        draw();
    }
//----------------------------------------------
    void draw(){
        gameView.setImageBitmap(blankBitmap);
        canvas.drawColor(Color.argb(255, 255, 255, 255));
        grid.drawGridLines(paint);
    }
//----------------------------------------------
    /*This part of the code will handle detecting that the user has tapped the screen.
    Note: only detects a user input on the grid not an object on the screen.*/
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            screen.coordinateConversion(motionEvent.getX(), motionEvent.getY(), grid.getBlockSize());
        }
        return true;
    }
//------------------------------------------------------
    //Handles the buttons.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.and):
                AND and = new AND();
                ImageView andImage = new ImageView(this);
                duplicateGate(andImage,"@drawable/and_gate",150,130);
                break;
            case (R.id.or):
                OR or = new OR();
                ImageView orImage = new ImageView(this);
                duplicateGate(orImage,"@drawable/or_gate",150,130);
                break;
            case (R.id.not):
                NOT not = new NOT();
                ImageView notImage = new ImageView(this);
                duplicateGate(notImage,"drawable/not_gate",150,130);
                break;
            case (R.id.toggle):
                Switch aSwitch = new Switch(false);
                ImageView toggleImage = new ImageView(this);
                duplicateGate(toggleImage,"@drawable/toggle_switch",137,154);
                break;
            case (R.id.light):
                ImageView ledImage = new ImageView(this);
                duplicateGate(ledImage,"@drawable/leds",137,154);
                break;
            //has not been implemented.
            case (R.id.delete):
                Toast.makeText(getApplicationContext(), "delete pressed", Toast.LENGTH_SHORT).show();
                break;
            //has not been implemented.
            case (R.id.run):
                Toast.makeText(getApplicationContext(), "run pressed", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.wire):
                Toast.makeText(getApplicationContext(), "wire pressed", Toast.LENGTH_SHORT).show();
                paint.setStrokeWidth(8);
                paint.setColor(Color.argb(255, 0, 0, 255));
                screen.drawLines(canvas,paint);
                break;
            //has not been implemented.
            case (R.id.save):
                Toast.makeText(getApplicationContext(), "save pressed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
//----------------------------------------------------------------
    //sets the image of the element created
    public void setImage(String name, ImageView image){
        int imageResource = getResources().getIdentifier(name,null,this.getPackageName());
        image.setImageResource(imageResource);
    }
//------------------------------------------------------------------
    //creates and displays an element.
    @SuppressLint("ClickableViewAccessibility")
    public void duplicateGate(ImageView image, String name, int width, int height){
        image.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
        mainLayout.addView(image);
        setImage(name,image);
        image.setOnTouchListener(this);
    }
//-----------------------------------------------------------------
    //Handles the touch of an element to move element around the screen.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int x = (int) motionEvent.getRawX();
        final int y = (int) motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;

                break;

            case MotionEvent.ACTION_UP:
                Toast.makeText(MainActivity.this,"gate moved", Toast.LENGTH_SHORT).show();
                position(x,y);
                break;

            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = x - xDelta;
                layoutParams.topMargin = y - yDelta;
                layoutParams.rightMargin = 0;
                layoutParams.bottomMargin = 0;
                view.setLayoutParams(layoutParams);
                break;
        }
        mainLayout.invalidate();

        return true;

    }
//------------------------------------------------------------------------------------
    //retrieves the touch coordinates of an element.
    void position(float touchX, float touchY){
        screen.gatePosition(touchX,touchY);
    }
}
