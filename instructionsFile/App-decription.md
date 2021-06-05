***\*随记APP说明文档\****

 

***\*一、开发环境和Logo说明\****

（1）Android Studio版本：

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps1.png) 

（2）SDK版本：

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps2.png) 

（3）Logo:

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps3.png) 

 

 

 

 

***\*二、App总体说明\****

**（1）*****\*app名称：\****

首先，app名称“随记”是一方面可以作为记事本使用，一方面可以记下自己某时刻的心情；

代码实现：

android:label="随记"

 

**（2）*****\*整体颜色设计：\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps4.png) 

使用android：theme属性指定了一个叫AppTheme的主题；

定义了一个叫AppTheme的主题，然后指定它的parent主题是Theme.AppCompat.DayNight,下面是一些属性的重写；

 

 

 

 

android:theme="@style/AppTheme"

 

<?xml version="1.0" encoding="utf-8"?><resources>    <style name="AppTheme" parent="Theme.AppCompat.DayNight">        <!-- Customize your theme here. -->        <!--状态栏颜色-->        <item name="android:windowLightStatusBar">true</item>                <item name="android:navigationBarColor">@color/navigationBarColor</item>        <!--设置背景颜色-->        <item name="android:windowBackground">@color/windowBackground</item>        <item name="colorPrimary">@color/colorPrimary</item>        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>        <item name="colorAccent">@color/colorAccent</item>    </style></resources>

 

<resources>
  <color name="colorPrimary">#f2f2f2</color>
  <color name="colorPrimaryDark">#f2f2f2</color>
  <color name="colorAccent">#000000</color>
  <color name="windowBackground">#f8f8f8</color>
  <color name="navigationBarColor">#f2f2f2</color>
</resources>

 

**（3）*****\*数据库的使用：使用SQLite进行数据库的存储；\****

 

***\*原理\****：

使用***\*SQLiteOpenHelper\****帮助类进行数据库的创建；

SQLiteOpenHelper帮助类是一个抽象类；里面有两个抽象方法：onCreate（）和onUpgrade(),我们必须在自己的帮助类里重写这两个方法，然后分别在这两个方法中实现创建和升级数据库的逻辑；

SQLiteOpenHelper还有***\*两个非常重要的实例方法\****：getReadableDatabase()和getWritableDatabase();这两个方法都可以创建和打开一个现有的数据库（如果数据库已存在则直接打开，否则要创建一个新的数据库），***\*并返回一个可对数据库进行读写操作的对象\****。不同的是，当数据库不可写入的时候（如磁盘空间不足），getReadableDatabase()方法返回的对象将以只读的方式打开数据库，而getWritableDatabase()方法则将出现异常。

SQLiteOpenHelper中***\*有两个构造方法可重写\****，使用参数少一点的那个构造方法。这个构造方法中接受四个参数：第一参数是Context,必须有它才能对数据库进行操作；第二个参数是数据库名，创建数据库时使用的就是这里指定的名称；第三个参数允许我们在查询数据的时候返回一个自定义的Cursor,***\*一般传入null即可\****；第四个参数表示当前数据库的版本号，可用于对数据库进行升级操作。构建出SQLiteOpenHelper的实例之后，再调用它的getReadableDatabase()方法或getWritableDatabase()方法创建数据库；

调用SQLiteOpenHelper的getReadableDatabase()和getWritableDatabase()方法;是可以创建和升级数据库的，而且两个方法还都会返回一个SQLiteDatabase对象，借助这个对象就可以对数据进行增、删、改、查操作；

***\*代码实现创建数据库：\****


public class NotesDB extends SQLiteOpenHelper {
  public static final String **TABLE_NAME** = "notes";
  public static final String **CONTENT** = "content";
  public static final String **PATH** = "path";
  public static final String **ID** = "_id";
  public static final String **TIME** = "time";
  public NotesDB(@Nullable Context context) {
    super(context, "notes", null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE note(" +
        "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
        "CONTENT TEXT NOT NULL," +
        "PATH TEXT," +
        "TIME TEXT NOT NULL)");

  }
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, 

int newVersion) {
  }
}

 

**（4）*****\*图片的添加\****

***\*逻辑代码：\****

FileProvider是一种特殊的ContentProvider;使用隐式Intent;

//创建File对象，用于存储拍照后的图片
phoneFile = new File(getExternalCacheDir(),getTime()+".jpg");//实例化File
if(phoneFile.exists()){
  phoneFile.delete();
}
try {
  phoneFile.createNewFile();
} catch (IOException e) {
  e.printStackTrace();
}
if(Build.VERSION.**SDK_INT**>=Build.VERSION_CODES.**N**){
  imageUri = FileProvider.**getUriForFile**(addContent.this, "com.example.Note.fileprovider", phoneFile);
}else{
  imageUri = Uri.**fromFile**(phoneFile);
}
//跳转到系统相机进行拍照
Intent i_img = new Intent("android.media.action.IMAGE_CAPTURE");
i_img.putExtra(MediaStore.**EXTRA_OUTPUT**, imageUri);
startActivityForResult(i_img,1);

 

protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  if(requestCode == 1){
    //通过Bitmap获取路径，展示内容
    Bitmap bitmap = null;
    try {
      bitmap = BitmapFactory.**decodeStream**(getContentResolver().openInputStream(imageUri));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    c_img.setImageBitmap(bitmap);//展示图片
  }
}

 

***\*在AndroidMainfest.xml注册provider：\****

<provider
  android:authorities="com.example.Note.fileprovider"
  android:name="androidx.core.content.FileProvider"
  android:exported="false"
  android:grantUriPermissions="true">
  <meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/file_paths" />
</provider>

 

***\*file_paths.xml：\****

<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
  <external-path
    name="my_images"
    path="/"/>
</paths>

 

 

 

 

 

 

 

 

 

**三、*****\*各个页面的布局及实现说明\****

 

***\*（一）主页面的实现：\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps5.png)      ![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps6.jpg)

***\*布局：\****

首先使用相对布局（RelativeLayout）,然后是一个ListView,下面两个Button，使用的是线性布局（LinearLayout）,权重都为1；

然后给ListView 的子项指定自定义的布局；自定义的布局整体是线性布局（LinearLayout）,然后里面又是一个LinearLayout包含一个LinearLayout和一个ImageView,最里面的LinearLayout包含两个TextView来显示文本和时间；

 

***\*主界面的布局文件：\****

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  xmlns:tools="http://schemas.android.com/tools"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical"
  tools:context=".MainActivity">

  <ListView
    android:id="@+id/list"
    android:layout_width="380dp"
    android:layout_height="680dp"
    android:layout_marginLeft="15dp"
    android:layout_marginTop="8dp"
    android:layout_marginRight="15dp"
    android:layout_marginBottom="?android:attr/actionBarSize"
    android:divider="@null"
    android:dividerHeight="8dp"></ListView>

  <LinearLayout
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:layout_alignParentBottom="true">

​    <Button
​      android:id="@+id/text"
​      android:layout_width="wrap_content"
​      android:layout_height="wrap_content"
​      android:layout_weight="1"
​      android:background="#f2f2f2"
​      android:drawableTop="@drawable/select_text"
​      android:text="文本"
​      android:paddingTop="10dp"
​      android:textColor="@color/btn_selector_color"
​      />

​    <Button
​      android:id="@+id/img"
​      android:layout_width="wrap_content"
​      android:layout_height="wrap_content"
​      android:layout_weight="1"
​      android:background="#f2f2f2"
​      android:drawableTop="@drawable/select_textphoto"
​      android:text="图文"
​      android:paddingTop="10dp"
​      android:textColor="@color/btn_selector_color" />
  </LinearLayout>
</RelativeLayout>

 

***\*子项的布局文件：\****

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:orientation="vertical" android:layout_width="match_parent"
android:layout_height="match_parent"
  \>

<LinearLayout
  android:layout_width="fill_parent"
  android:layout_height="wrap_content"
  android:orientation="horizontal"
  android:background="#ffffff"
  \>

  <LinearLayout
    android:layout_width="215dp"
    android:layout_height="90dp"
    android:gravity="center_vertical"
    android:orientation="vertical">

​    <TextView
​      android:id="@+id/list_content"
​      android:layout_width="wrap_content"
​      android:layout_height="wrap_content"
​      android:text="tv"
​      android:textColor="#000000" />

​    <TextView
​      android:id="@+id/list_time"
​      android:layout_width="wrap_content"
​      android:layout_height="wrap_content"
​      android:text="time"
​      android:textColor="#939393" />

  </LinearLayout>

  <ImageView
    android:id="@+id/list_img"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginLeft="60dp" />

</LinearLayout>
</LinearLayout>

 

***\*逻辑实现：\****

首先，创建一个自定义的适配器，这个适配器继承自BaseAdapter,主构造函数MyAdapter，用于将Activity的实例、数据源传递进来；再重写getView()方法，这个方法在每个子项被滚动到屏幕内的时候被调用；在getView()方法中，首先使用LayoutInflater来为这个子项加载布局；接下来调用view的findViewById()方法获取ImageView和TextView的实例，然后使用Cursor类的方法得到当前项实例，再设置图片和文字，最后将布局返回；点击每一个ListView子项就会进入到详情页面；

 

代码：

***\*MyAdapter.java文件：\****


//适配器
public class MyAdapter extends BaseAdapter {
  private Context context;
  private Cursor cursor;
  private LinearLayout layout;
  public MyAdapter(Context context,Cursor cursor){
    this.context = context;
    this.cursor = cursor;

  }
  @Override
  public int getCount() {
    return cursor.getCount();
  }

  @Override
  public Object getItem(int position) {
    return cursor.getPosition();
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
     //加载视图的权限
    LayoutInflater inflater = LayoutInflater.**from**(context);
    layout = (LinearLayout) inflater.inflate(R.layout.**cell**,null);
    TextView contentTv = layout.findViewById(R.id.**list_content**);
    TextView timeTv = layout.findViewById(R.id.**list_time**);
    ImageView imgiv = layout.findViewById(R.id.**list_img**);

​    cursor.moveToPosition(position);
​    String content = cursor.getString(cursor.getColumnIndex("CONTENT"));
​    String time = cursor.getString(cursor.getColumnIndex("TIME"));
​    String url = cursor.getString(cursor.getColumnIndex("PATH"));

​    contentTv.setText(content);
​    timeTv.setText(time);
​    imgiv.setImageBitmap(getImageThumbnail(url,300,300));

​    return layout;
  }

  //通过这个方法获取当前的缩略图
  public Bitmap getImageThumbnail(String uri, int width,int height){
    Bitmap bitmap = null;
    //获取缩略图
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    bitmap = BitmapFactory.**decodeFile**(uri,options);

​    options.inJustDecodeBounds = false;
​    int beWidth = options.outWidth/width;
​    int beHeight = options.outHeight/height;
​    int be = 1;
​    if(beWidth<beHeight){
​      be = beWidth;
​    }else {
​      be = beHeight;
​    }
​    if(be<=0){
​      be = 1;
​    }
​    options.inSampleSize = be;
​    bitmap = BitmapFactory.**decodeFile**(uri,options);
​    bitmap = ThumbnailUtils.**extractThumbnail**(bitmap,width,height,ThumbnailUtils.**OPTIONS_RECYCLE_INPUT**);
​    return bitmap;
  }
}

 

***\*MainActivity.java文件：\****


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private Button textbtn,imgbtn;
  private ListView lv;
  private Intent i;
  private MyAdapter adapter;
  private NotesDB notesDB;
  private SQLiteDatabase dbReader;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.**activity_main**);
    //初始化
    textbtn = findViewById(R.id.**text**);
    imgbtn = findViewById(R.id.**img**);
    lv = findViewById(R.id.**list**);

​    //添加监听事件
​    textbtn.setOnClickListener(this);
​    imgbtn.setOnClickListener(this);


    notesDB = new NotesDB(this);
    dbReader = notesDB.getReadableDatabase();//获取读取权限


    //进入详情页
    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      private Cursor cursor = dbReader.query("note",null,null,null,null,null,null);

​      @Override
​      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
​        if(cursor.moveToFirst()){
​        cursor.moveToPosition(position);
​        }
​        Intent i = new Intent(MainActivity.this,SelectAct.class);
​        i.putExtra("ID",cursor.getInt(cursor.getColumnIndex("ID")));
​        i.putExtra("CONTENT",cursor.getString(cursor.getColumnIndex("CONTENT")));
​        i.putExtra("TIME",cursor.getString(cursor.getColumnIndex("TIME")));
​        i.putExtra("PATH",cursor.getString(cursor.getColumnIndex("PATH")));
​        i.putExtra("VIDEO",cursor.getString(cursor.getColumnIndex("VIDEO")));
​        startActivity(i);

​      }
​    });
  }

  @RequiresApi(api = Build.VERSION_CODES.**JELLY_BEAN**)
  @Override
  public void onClick(View v) {
    i = new Intent(this,addContent.class);//实例化
    switch (v.getId()){
      case R.id.**text**:
        i.putExtra("flag","1");
        startActivity(i);
        break;

​      case R.id.**img**:
​        i.putExtra("flag","2");
​        startActivity(i);
​        break;

​    }

  }

  public void selectDB(){
    Cursor cursor = dbReader.query("note",null,null
    ,null,null,null,null);
    adapter = new MyAdapter(this,cursor);
    lv.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    selectDB();
  }
}

然后是两个按钮是分别添加监听事件，跳转到另一个Activity来添加内容；***\*每个按钮被点击的那一个过程，按钮会有文字和图片颜色的变化，来提醒确实点击了某个按钮；\****

<selector xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:state_pressed="true" android:color="@color/state_pressed"/>
  <item android:state_checkable="false" android:color="@color/state_checkable"></item>
</selector>

 

<selector xmlns:android="http://schemas.android.com/apk/res/android">
  <item android:state_window_focused="false" android:drawable="@drawable/cancel"></item>
  <item android:state_pressed="true" android:drawable="@drawable/cancel1"></item>
  <item android:state_checkable="false" android:drawable="@drawable/cancel"></item>
</selector>

 

***\*（二）添加内容界面说明：\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps7.png)***\*布局\*******\*：\****

首先是LinearLayout（线性布局），然后是一个ImageView和一个EditView，下面两个按钮是LinearLayout布局，权重都为1；

 

 

 

 

 

 

 

 

 

 

 

 

 

 

 

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  xmlns:tools="http://schemas.android.com/tools"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical"
  tools:context=".addContent">
  <ImageView
    android:layout_width="300dp"
    android:layout_height="300dp"
    android:id="@+id/c_img"
    android:visibility="gone"
    />
  <EditText
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:background="@null"
    android:gravity="top"
    android:id="@+id/etText"
    android:hint="记录生活的事情......"
    />
  <LinearLayout
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    \>
    <Button
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:id="@+id/save"
      android:layout_weight="1"
      android:textColor="@color/btn_selector_color"
      android:background="#f2f2f2"
      android:drawableTop="@drawable/select_save"
      android:paddingTop="10dp"
      android:text="保存"
      />
    <Button
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:id="@+id/cancel"
      android:layout_weight="1"
      android:textColor="@color/btn_selector_color"
      android:background="#f2f2f2"
      android:drawableTop="@drawable/select_cancel"
      android:paddingTop="10dp"
      android:text="取消"
      />

  </LinearLayout>

</LinearLayout>

 

 

 

***\*逻辑实现：\****

首先创建数据库对象，准备添加数据；刚开始会先判断点击的按钮是文字按钮还是图文按钮；

如果是图文按钮会跳转到系统相机进行拍照，拍照之后可以再添加文字；根据sdk版本的不同，添加方式也有所不同；然后保存按钮会执行添加数据的一部分代码，首先会创建ContentValues对象来封装数据，最后调用insert()方法添加数据；点击取消按钮，会跳转到前一个Activity；

 

public class addContent extends AppCompatActivity implements View.OnClickListener {
  private static final String **TAG** =".addContent";
  private String val;
  private Button savebtn,cancelbtn;
  private EditText etText;
  private ImageView c_img;
  private Uri imageUri;

  //创建数据库对象，准备添加数据
  private NotesDB notesDB;
  private SQLiteDatabase dbWriter;

  private File phoneFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.**activity_add_content**);

​    val = getIntent().getStringExtra("flag");
​    Log.**i**(**TAG**,"这个值是："+val);

​    //初始化
​    savebtn = findViewById(R.id.**save**);
​    cancelbtn = findViewById(R.id.**cancel**);
​    etText = findViewById(R.id.**etText**);
​    c_img = findViewById(R.id.**c_img**);


    notesDB = new NotesDB(this);
    dbWriter = notesDB.getWritableDatabase();//获取写入数据的权限

​    //添加监听事件
​    savebtn.setOnClickListener(this);
​    cancelbtn.setOnClickListener(this);
​    initView();
  }

  public void initView(){
    if(val.equals("1")){//添加文字
      c_img.setVisibility(View.**GONE**);//隐藏
    }
    if(val.equals("2")){//图片
      c_img.setVisibility(View.**VISIBLE**);//显示
      //创建File对象，用于存储拍照后的图片
      phoneFile = new File(getExternalCacheDir(),getTime()+".jpg");//实例化File
      if(phoneFile.exists()){
        phoneFile.delete();
      }
      try {
        phoneFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if(Build.VERSION.**SDK_INT**>=Build.VERSION_CODES.**N**){
        imageUri = FileProvider.**getUriForFile**(addContent.this, "com.example.Note.fileprovider", phoneFile);
      }else{
        imageUri = Uri.**fromFile**(phoneFile);
      }
      //跳转到系统相机进行拍照
      Intent i_img = new Intent("android.media.action.IMAGE_CAPTURE");
      i_img.putExtra(MediaStore.**EXTRA_OUTPUT**, imageUri);
      startActivityForResult(i_img,1);
    }

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.**save**:
        addDB();
        finish();
        break;
      case R.id.**cancel**:
        finish();
        break;
    }
  }

  //添加数据
  public void addDB(){
    ContentValues cv = new ContentValues();//创建ContentValues对象来封装数据
    cv.put("CONTENT",etText.getText().toString());//调用ContentValues的put方法
    cv.put("TIME",getTime());
    cv.put("PATH",phoneFile+"");//插入到数据库当中
    dbWriter.insert("note",null,cv);//insert()方法插入数据
  }

  //获取时间
  public String getTime(){
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    Date date = new Date();
    String str = format.format(date);
    return str;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == 1){
      //通过Bitmap获取路径，展示内容
      Bitmap bitmap = null;
      try {
        bitmap = BitmapFactory.**decodeStream**(getContentResolver().openInputStream(imageUri));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      c_img.setImageBitmap(bitmap);//展示图片
    }
  }
}

 

 

***\*（三）详情页说明：\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps8.png)     ![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps9.png)

 

***\*布局：\****

整体是LinearLayout（线性布局），然后是两个TextView，下面两个按钮是LinearLayout，权重为1；

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"    android:orientation="vertical" android:layout_width="match_parent"    android:layout_height="match_parent">    <TextView        android:layout_width="fill_parent"        android:layout_height="wrap_content"        android:id="@+id/s_time"        android:textColor="#939393"        />    <ImageView        android:layout_width="300dp"        android:layout_height="300dp"        android:id="@+id/s_img"        android:visibility="gone"        />    <TextView        android:layout_width="fill_parent"        android:layout_height="wrap_content"        android:id="@+id/s_tv"        android:layout_weight="1"        android:textColor="#000000"        />    <LinearLayout        android:layout_width="fill_parent"        android:layout_height="wrap_content"        android:orientation="horizontal"        >        <Button            android:layout_width="wrap_content"            android:layout_height="wrap_content"            android:id="@+id/deletebtn"            android:text="删除"            android:drawableTop="@drawable/select_delete"            android:paddingTop="10dp"            android:background="#f2f2f2"            android:textColor="@color/btn_selector_color"            android:layout_weight="1"            />        <Button            android:layout_width="wrap_content"            android:layout_height="wrap_content"            android:id="@+id/returnbtn"            android:text="返回"            android:drawableTop="@drawable/select_return"            android:paddingTop="10dp"            android:background="#f2f2f2"            android:textColor="@color/btn_selector_color"            android:layout_weight="1"            />    </LinearLayout></LinearLayout>

 

 

***\*逻辑实现：\****

从数据库获取到数据，然后通过设置显示获取到的当前时间和图片与文字；

下面的按钮删除，会调用delete()方法删除当前数据，接着执行notify()方法刷新视图；

返回按钮点击后跳转到前一个Activity；

public class SelectAct extends AppCompatActivity implements View.OnClickListener {
  private Button s_delete, s_back;
  private ImageView s_img;
  private TextView s_tv,s_time;
  private NotesDB notesDB;//创建数据库对象
  private SQLiteDatabase dbWriter;//获取写的权限

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.**select**);
    int i = getIntent().getIntExtra("ID",0);
    System.**out**.println(i);
    s_delete = findViewById(R.id.**deletebtn**);
    s_back = findViewById(R.id.**returnbtn**);
    s_img = findViewById(R.id.**s_img**);
    s_tv = findViewById(R.id.**s_tv**);
    s_time = findViewById(R.id.**s_time**);

​    notesDB = new NotesDB(this);//实例化
​    dbWriter = notesDB.getWritableDatabase();//获取写的权限


    //添加按钮的监听事件
    s_delete.setOnClickListener(this);
    s_back.setOnClickListener(this);

​    //进行判断
​    if(getIntent().getStringExtra("PATH").equals("null")){
​      s_img.setVisibility(View.**GONE**);
​    }else{
​      s_img.setVisibility(View.**VISIBLE**);
​    }


    s_tv.setText(getIntent().getStringExtra("CONTENT"));//显示文字内容
    s_time.setText(getIntent().getStringExtra("TIME"));

​    Bitmap bitmap = BitmapFactory.**decodeFile**(getIntent()
​        .getStringExtra("PATH"));
​    s_img.setImageBitmap(bitmap);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.**deletebtn**:
        dbWriter.delete("note","ID="+
            getIntent().getIntExtra("ID",0),null);
        notify();
        System.**out**.println("删除的是："+getIntent().getIntExtra("ID",0));
        //finish();
        break;
      case R.id.**returnbtn**:
        finish();
        break;
    }
  }
}

 

 

***\*四、所用所学知识点\****

***\*第1章：概述和开发环境\****

进行了开发环境的搭建；

将创建的项目部署到虚拟机中；

 

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps10.png) 

 

 

 

 

**第2章** ***\*应用的资源访问\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps11.png) 

***\*color目录：\****

自定义的目录，里面存放的是按钮点击改变颜色的资源文件；

 

***\*Drawable目录：\****

存放各种图片资源以及点击之后改变图片的资源文件；

 

***\*Layout目录：\****

存放所有的布局文件；

 

***\*xml目录：\****

自定义目录，存放了添加图片时的所用的资源文件；

 

***\*values\*******\*目录：\*******\*
\****Android应用界面上需要显示各类文本标签的文字信息（strings，styles，colors）;

如主题界面的颜色资源，就在values目录下的colors.xml文件中；

 

 

**第3章** ***\*第4章：\*******\*用户界面编程开发\*******\*、\*******\*服务开发和广播事件\****

***\*（1）实现了Activity的创建，Activity的生命周期：\****

![img](file:///C:\Users\zrf\AppData\Local\Temp\ksohtml13444\wps12.png) 

 

***\*（2）实现了不同Activity之间的调用和数据交换；比如：\****

public void onClick(View v) {
  i = new Intent(this,addContent.class);//实例化
  switch (v.getId()){
    case R.id.**text**:
      i.putExtra("flag","1");
      startActivity(i);
      break;

​    case R.id.**img**:
​      i.putExtra("flag","2");
​      startActivity(i);
​      break;
  }
}

 

val = getIntent().getStringExtra("flag");
Log.**i**(**TAG**,"这个值是："+val);

 

***\*（3）在AndroidManifest.xml中进行了Activity的注册，比如：\****

<activity
  android:name=".MainActivity"
  android:screenOrientation="portrait">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
  </intent-filter>
</activity>

 

 

***\*第5章\**** ***\*Android数据存储和数据共享\****

***\*这个app的开发主要使用了SQLite进行数据的增删改查的操作；\****

***\*运用了SharedPerence的读写方法和存储方法；比如：\****

***\*数据库的创建以及后面的添加删除等操作，\****

public class NotesDB extends SQLiteOpenHelper {
  public static final String **TABLE_NAME** = "notes";
  public static final String **CONTENT** = "content";
  public static final String **PATH** = "path";
  public static final String **VIDEO** = "video";
  public static final String **ID** = "_id";
  public static final String **TIME** = "time";
  public NotesDB(@Nullable Context context) {
    super(context, "notes", null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE note(" +
        "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
        "CONTENT TEXT NOT NULL," +
        "PATH TEXT," +
        "VIDEO TEXT," +
        "TIME TEXT NOT NULL)");

  }
}

 

 

 

 

**第6章** ***\*Android多媒体应用开发\****

***\*实现了ListView的使用；\****

***\*实现了添加图片的操作；\****

<ListView
  android:id="@+id/list"
  android:layout_width="380dp"
  android:layout_height="680dp"
  android:layout_marginLeft="15dp"
  android:layout_marginTop="8dp"
  android:layout_marginRight="15dp"
  android:layout_marginBottom="?android:attr/actionBarSize"
  android:divider="@null"
  android:dividerHeight="8dp"></ListView>

 

 

 

 

 