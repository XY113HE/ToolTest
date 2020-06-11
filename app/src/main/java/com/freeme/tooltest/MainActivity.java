package com.freeme.tooltest;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TimePickerView pvTime;
    private ImageView img1, img2, img3;
    private boolean[] imgHere = new boolean[]{false, false, false};
    private int currentImg = 0;
    private TextView imgCount;
    private List<String> imgUrl = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatuBar();
        setContentView(R.layout.activity_main);
        checkPerm();
        initTimeSelcet();
        initSpinSelect();
        initImageSelect();
    }

    private void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void setStatuBar() {
        //设置 paddingTop
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
//        rootView.setPadding(0, getStatusBarHeight(), 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以上直接设置状态栏颜色
            getWindow().setStatusBarColor(Color.WHITE);
        } else {
            //根布局添加占位状态栏
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight());
            statusBarView.setBackgroundColor(Color.WHITE);
            decorView.addView(statusBarView, lp);
        }
        setAndroidNativeLightStatusBar(true);
    }


    private void checkPerm() {
        TedPermission.with(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                })
                .check();
    }

    public String getImageCachePath()//给图片一个存储路径
    {
//        if (!isExistSDCard())
//        {
//            return "";
//        }

        String sdRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        String result = sdRoot +
                "/" + "CarProtect" + "/" + "Cache";
        if (new File(result).exists() && new File(result).isDirectory())
        {
            return result;
        }
        else {
            return sdRoot;
        }
    }

    private void initImageSelect() {
        imgCount = findViewById(R.id.img_count);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imgHere[0]) {
                    selectImg();
                }else{
                    deleteImg(0);
                }
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imgHere[1]) {
                    selectImg();
                }else{
                    deleteImg(1);
                }
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imgHere[2]) {
                    selectImg();
                }else{
                    deleteImg(2);
                }
            }
        });
    }

    private void deleteImg(int i) {
        imgUrl.remove(i);
        switch (imgUrl.size()){
            case 0:
                img1.setImageResource(R.drawable.img_add);
                img2.setImageResource(R.drawable.img_add);
                img3.setImageResource(R.drawable.img_add);
                img2.setVisibility(View.INVISIBLE);
                img3.setVisibility(View.INVISIBLE);
                currentImg = 0;
                imgCount.setText(currentImg+" / 3");
                imgHere = new boolean[]{false, false, false};
                break;
            case 1:
                img2.setImageResource(R.drawable.img_add);
                img3.setImageResource(R.drawable.img_add);
                img2.setVisibility(View.VISIBLE);
                img3.setVisibility(View.INVISIBLE);
                currentImg = 1;
                imgCount.setText(currentImg+" / 3");
                imgHere = new boolean[]{true, false, false};
                Bitmap bitImage = BitmapFactory.decodeFile(imgUrl.get(0));
                img1.setImageBitmap(bitImage);
                break;
            case 2:
                img3.setImageResource(R.drawable.img_add);
                img3.setVisibility(View.VISIBLE);
                currentImg = 2;
                imgCount.setText(currentImg+" / 3");
                imgHere = new boolean[]{true, true, false};
                Bitmap bitImage1 = BitmapFactory.decodeFile(imgUrl.get(0));
                img1.setImageBitmap(bitImage1);
                Bitmap bitImage2 = BitmapFactory.decodeFile(imgUrl.get(1));
                img2.setImageBitmap(bitImage2);
                break;
        }
    }

    private void selectImg() {
        Uri imageUri = Uri.fromFile(new File(getImageCachePath(),
                "fc_image.jpg"));

        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
//                openAlbumIntent.putExtra("crop", "true");
        // openAlbumIntent.putExtra("aspectX", 1);
        // openAlbumIntent.putExtra("aspectY", 1);
        // openAlbumIntent.putExtra("outputX", 240);
        // openAlbumIntent.putExtra("outputY", 240);
        // openAlbumIntent.putExtra("return-data", true);

        // lixi 直接从图库选择图片。
//                openAlbumIntent.putExtra("output", imageUri);
//                openAlbumIntent.putExtra("outputFormat", "JPEG");

        startActivityForResult(openAlbumIntent, 1);
    }

    private void initSpinSelect() {
        final String[] list = new String[]{"请选择周期","身份证","学生证","军人证","工作证","其他"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spin, list);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//
//        });
    }

    private void initTimeSelcet() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2029, 11, 28);
        //时间选择器
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                TextView btn = (TextView) v;
                btn.setText(getTimes(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "", "")
                .isCenterLabel(true)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setDecorView(null)
                .build();
    }

    public void dateSelect(View view) {
        pvTime.show(view);
    }

    private String getTimes(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {

            switch (requestCode) {

//                case TAKE_PICTURE:
//
//                    ImageTools.ClearNewTempImages(this.getActivity());
//                    // 判断图片还在不在？
//                    String sdPath = FileUtil.getImageCachePath();
//                    if (!ImageTools.findPhotoFromSDCard(sdPath, "f_c_image")) {
//                        //  拍照的图片没有保存到本地目录下。
//                        // TODO：这里要提示一个文言。
//                        Toast.makeText(getActivity(), "拍照失败!", Toast.LENGTH_SHORT)
//                                .show();
//
//
//                        //ImageTools.deleteLatestPhoto(this.getActivity());
//                        return;
//
//                    }
//
//                    //ImageTools.deleteLatestPhoto(this.getActivity());
//                    // 会莫名其妙的启动两次？
//                    // 启动图片预览和保存的界面。
//                    bundle.putInt("CurrentPage", TAKE_PICTURE);
//
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
//
//
//
//                    break;

                case 1:
                    handleImageOnkitKat(data);


                    break;

                default:
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnkitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android,providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        Log.e("lmy", "displayImage: " + imagePath);
        if (imagePath != null) {
            imgUrl.add(imagePath);
            Bitmap bitImage = BitmapFactory.decodeFile(imagePath);//格式化图片
            currentImg++;
            imgCount.setText(currentImg+" / 3");
            if(!imgHere[0]){
                img1.setImageBitmap(bitImage);//为imageView设置图片
                img2.setVisibility(View.VISIBLE);
                imgHere[0] = true;
            }else if(!imgHere[1]){
                img2.setImageBitmap(bitImage);//为imageView设置图片
                img3.setVisibility(View.VISIBLE);
                imgHere[1] = true;
            }else{
                img3.setImageBitmap(bitImage);//为imageView设置图片
                imgHere[2] = true;
            }
            //img1.setImageBitmap(bitImage);//为imageView设置图片


        } else {
            Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    //获得图片路径
    public String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);   //内容提供器
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //获取路径
            }
        }
        cursor.close();
        return path;
    }

    /**
     * 利用反射获取状态栏高度
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void nextActivity(View view){
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}
