package it.moondroid.gestures.example;

import java.io.File;
import java.util.List;
import java.util.Set;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

 

public class MainActivity extends Activity {
	private GestureOverlayView gov; 
	private Gesture gesture; 
	private GestureLibrary gestureLib; 
	private TextView tv;
	private EditText et;
	private String path; 
	private File file; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		tv = (TextView) findViewById(R.id.himi_tv);
		et = (EditText) findViewById(R.id.himi_edit);
		gov = (GestureOverlayView) findViewById(R.id.himi_gesture);
		gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE); 
		 
		path = new File(Environment.getExternalStorageDirectory(), "gestures").getAbsolutePath();
		 
		file = new File(path); 
		gestureLib = GestureLibraries.fromFile(path); 
		gov.addOnGestureListener(new OnGestureListener() {  
					@Override
					 
					public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
						tv.setText("请您在紧凑的时间内用两笔划来完成一个手势！");
					} 
					@Override
				 
					public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
						gesture = overlay.getGesture() 
						if (gesture.getStrokesCount() == 2) { 
						 
							if (event.getAction() == MotionEvent.ACTION_UP) { 
								 
								if (et.getText().toString().equals("")) {
									tv.setText("由于您没有输入手势名称，保存失败");
								} else {
									tv.setText("正在保存手势...");
									addMyGesture(et.getText().toString(), gesture);  
								}
							}
						} else {
							tv.setText("请您在紧凑的时间内用两笔划来完成一个手势！");
						}
					} 
					@Override
					public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
					} 
					@Override
					public void onGesture(GestureOverlayView overlay, MotionEvent event) {
					}
				});
	 
		if (!gestureLib.load()) {
			tv.setText("Himi提示： ");
		} else {
			Set<String> set = gestureLib.getGestureEntries(); 
			Object ob[] = set.toArray();
			loadAllGesture(set, ob);
		}
	}

	public void addMyGesture(String name, Gesture gesture) { 
		try {
			if (name.equals("himi")) {
				findGesture(gesture);
			} else {
			 
				if (Environment.getExternalStorageState() != null) { 
					if (!file.exists()) { 
						gestureLib.addGesture(name, gesture);
						if (gestureLib.save()) { 
							gov.clear(true); 
						 
							tv.setText("保存手势成功！因为不存在手势文件，" + "所以第一次保存手势成功会默认先创" +
									"建了一个手势文件！然后将手势保存到文件中.");
							et.setText("");
							gestureToImage(gesture);
						} else {
							tv.setText("保存手势失败！");
						}
					} else {  
						if (!gestureLib.load()) { 
							tv.setText("手势文件读取失败！");
						} else  
							Set<String> set = gestureLib.getGestureEntries(); 
							Object ob[] = set.toArray();
							boolean isHavedGesture = false;
							for (int i = 0; i < ob.length; i++) { 
								if (((String) ob[i]).equals(name)) { 
									isHavedGesture = true;
								}
							}
							if (isHavedGesture) { 
							   gestureLib.removeEntry(name);
							  gestureLib.addGesture(name, gesture);
							} else {
								gestureLib.addGesture(name, gesture);
							}
							if (gestureLib.save()) {
								gov.clear(true); 
								gestureToImage(gesture);
								tv.setText("保存手势成功！当前所有手势一共有：" + ob.length + "个");
								et.setText("");
							} else {
								tv.setText("保存手势失败！");
							}
						 
							if (ob.length > 9) {
								for (int i = 0; i < ob.length; i++) { 
									gestureLib.removeEntry((String) ob[i]);
								}
								gestureLib.save();
								if (MySurfaceView.vec_bmp != null) {
									MySurfaceView.vec_bmp.removeAllElements(); 
								}
								tv.setText("手势超过9个，已全部清空!");
								et.setText("");
							}
							ob = null;
							set = null;
						}
					}
				} else {
					tv.setText("当前模拟器没有SD卡 - -。");
				}
			}
		} catch (Exception e) {
			tv.setText("操作异常！");
		}
	}

	public void loadAllGesture(Set<String> set, Object ob[]) {  
		if (gestureLib.load()) { 
			set = gestureLib.getGestureEntries(); 
			ob = set.toArray();
			for (int i = 0; i < ob.length; i++) {
				 
				gestureToImage(gestureLib.getGestures((String) ob[i]).get(0));
			 
				MySurfaceView.vec_string.addElement((String) ob[i]);
			}
		}
	}

	public void gestureToImage(Gesture ges) { 
		 
		if (MySurfaceView.vec_bmp != null) {
			MySurfaceView.vec_bmp.addElement(ges.toBitmap(100, 100, 12, Color.GREEN));
		}
	}

	public void findGesture(Gesture gesture) {
		try {
			 
			if (Environment.getExternalStorageState() != null) { 
				if (!file.exists()) { 
					tv.setText("匹配手势失败，因为手势文件不存在！！");

				} else { 
					 
					if (!gestureLib.load()) { 
						tv.setText("匹配手势失败，手势文件读取失败！");
					} else { 
						List<Prediction> predictions = gestureLib.recognize(gesture);
						 
						if (!predictions.isEmpty()) {
							Prediction prediction = predictions.get(0);
							  
							if (prediction.score >= 1) {
								tv.setText("当前你的手势在手势库中找到最相似的手势：name =" + prediction.name);
							}
						}
					}
				}
			} else {
				tv.setText("匹配手势失败，,当前模拟器没有SD卡 - -。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			tv.setText("由于出现异常，匹配手势失败啦");
		}
	}
}
