package com.amassfreight.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.amassfreight.utils.BitmapUtil;
import com.amassfreight.utils.MathUtil;
import com.amassfreight.utils.RoundUtil;
import com.amassfreight.warehouse.R;

import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


/**
 * 
 * �Ź������
 * 
 * @author way
 * 
 */
public class SquaredPassWord extends View {
	private float w = 0;
	private float h = 0;

	//
	private boolean isCache = false;
	//
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	//
	private Point[][] mPoints = new Point[3][3];
	// Բ�İ뾶
	private float r = 0;
	// ѡ�еĵ�
	private List<Point> sPoints = new ArrayList<Point>();
	private boolean checking = false;
	private Bitmap locus_round_original;//Բ���ʼ״̬ʱ��ͼƬ
	private Bitmap locus_round_click;//Բ����ʱ��ͼƬ
	private Bitmap locus_round_click_error;//����ʱԲ���ͼƬ
	private Bitmap locus_line;//��״̬���ߵ�ͼƬ
	private Bitmap locus_line_semicircle;
	private Bitmap locus_line_semicircle_error;
	private Bitmap locus_arrow;//�ߵ��ƶ�����
	private Bitmap locus_line_error;//����״̬�µ��ߵ�ͼƬ
	private long CLEAR_TIME = 0;//���ۼ���ʱ��
	private int passwordMinLength = 5;//������С����
	private boolean isTouch = true; // �Ƿ�ɲ���
	private Matrix mMatrix = new Matrix();
	private int lineAlpha = 50;//���ߵ�͸����

	public SquaredPassWord(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquaredPassWord(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquaredPassWord(Context context) {
		super(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isCache) {
			initCache();
		}
		drawToCanvas(canvas);
	}

	private void drawToCanvas(Canvas canvas) {

		// mPaint.setColor(Color.RED);
		// Point p1 = mPoints[1][1];
		// Rect r1 = new Rect(p1.x - r,p1.y - r,p1.x +
		// locus_round_click.getWidth() - r,p1.y+locus_round_click.getHeight()-
		// r);
		// canvas.drawRect(r1, mPaint);
		// �����е�
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (p.state == Point.STATE_CHECK) {
					canvas.drawBitmap(locus_round_click, p.x - r, p.y - r,
							mPaint);
				} else if (p.state == Point.STATE_CHECK_ERROR) {
					canvas.drawBitmap(locus_round_click_error, p.x - r,
							p.y - r, mPaint);
				} else {
					canvas.drawBitmap(locus_round_original, p.x - r, p.y - r,
							mPaint);
				}
			}
		}
		// mPaint.setColor(Color.BLUE);
		// canvas.drawLine(r1.left+r1.width()/2, r1.top, r1.left+r1.width()/2,
		// r1.bottom, mPaint);
		// canvas.drawLine(r1.left, r1.top+r1.height()/2, r1.right,
		// r1.bottom-r1.height()/2, mPaint);

		// ������
		if (sPoints.size() > 0) {
			int tmpAlpha = mPaint.getAlpha();
			mPaint.setAlpha(lineAlpha);
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				Point p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (this.movingNoPoint) {
				drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
			}
			mPaint.setAlpha(tmpAlpha);
			lineAlpha = mPaint.getAlpha();
		}

	}

	/**
	 * ��ʼ��Cache��Ϣ
	 * 
	 * @param canvas
	 */
	private void initCache() {

		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;

		if (w > h) {
			x = (w - h) / 2;
			w = h;
		}
		// ����
		else {
			y = (h - w) / 2;
			h = w;
		}

		locus_round_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_round_original);
		locus_round_click = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_round_click);
		locus_round_click_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_round_click_error);

		locus_line = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_line);
		locus_line_semicircle = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_line_semicircle);

		locus_line_error = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_line_error);
		locus_line_semicircle_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.locus_line_semicircle_error);

		locus_arrow = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.locus_arrow);
		// Log.d("Canvas w h :", "w:" + w + " h:" + h);

		// ����ԲȦͼƬ�Ĵ�С
		float canvasMinW = w;
		if (w > h) {
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2;
		float roundW = roundMinW / 2.f;
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;

		if (locus_round_original.getWidth() > roundMinW) {
			float sf = roundMinW * 1.0f / locus_round_original.getWidth(); // ȡ�����ű������е�ͼƬ��������
			locus_round_original = BitmapUtil.zoom(locus_round_original, sf);
			locus_round_click = BitmapUtil.zoom(locus_round_click, sf);
			locus_round_click_error = BitmapUtil.zoom(locus_round_click_error,
					sf);

			locus_line = BitmapUtil.zoom(locus_line, sf);
			locus_line_semicircle = BitmapUtil.zoom(locus_line_semicircle, sf);

			locus_line_error = BitmapUtil.zoom(locus_line_error, sf);
			locus_line_semicircle_error = BitmapUtil.zoom(
					locus_line_semicircle_error, sf);
			locus_arrow = BitmapUtil.zoom(locus_arrow, sf);
			roundW = locus_round_original.getWidth() / 2;
		}

		mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
		mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
		mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
		mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
		mPoints[1][1] = new Point(x + w / 2, y + h / 2);
		mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
		mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
		mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
		mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
		int k = 0;
		for (Point[] ps : mPoints) {
			for (Point p : ps) {
				p.index = k;
				k++;
			}
		}
		r = locus_round_original.getHeight() / 2;// roundW;
		isCache = true;
	}

	/**
	 * �����������
	 * 
	 * @param canvas
	 * @param a
	 * @param b
	 */
	private void drawLine(Canvas canvas, Point a, Point b) {
		float ah = (float) MathUtil.distance(a.x, a.y, b.x, b.y);
		float degrees = getDegrees(a, b);
		// Log.d("=============x===========", "rotate:" + degrees);
		canvas.rotate(degrees, a.x, a.y);

		if (a.state == Point.STATE_CHECK_ERROR) {
			mMatrix.setScale((ah - locus_line_semicircle_error.getWidth())
					/ locus_line_error.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line_error.getHeight()
					/ 2.0f);
			canvas.drawBitmap(locus_line_error, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle_error, a.x
					+ locus_line_error.getWidth(),
					a.y - locus_line_error.getHeight() / 2.0f, mPaint);
		} else {
			mMatrix.setScale((ah - locus_line_semicircle.getWidth())
					/ locus_line.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line.getHeight() / 2.0f);
			canvas.drawBitmap(locus_line, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle, a.x + ah
					- locus_line_semicircle.getWidth(),
					a.y - locus_line.getHeight() / 2.0f, mPaint);
		}

		canvas.drawBitmap(locus_arrow, a.x, a.y - locus_arrow.getHeight()
				/ 2.0f, mPaint);

		canvas.rotate(-degrees, a.x, a.y);

	}

	public float getDegrees(Point a, Point b) {
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = b.x;// b.index % 3;
		float by = b.y;// b.index / 3;
		float degrees = 0;
		if (bx == ax) // y����� 90�Ȼ�270
		{
			if (by > ay) // ��y����±� 90
			{
				degrees = 90;
			} else if (by < ay) // ��y����ϱ� 270
			{
				degrees = 270;
			}
		} else if (by == ay) // y����� 0�Ȼ�180
		{
			if (bx > ax) // ��y����±� 90
			{
				degrees = 0;
			} else if (bx < ax) // ��y����ϱ� 270
			{
				degrees = 180;
			}
		} else {
			if (bx > ax) // ��y����ұ� 270~90
			{
				if (by > ay) // ��y����±� 0 - 90
				{
					degrees = 0;
					degrees = degrees
							+ switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				} else if (by < ay) // ��y����ϱ� 270~0
				{
					degrees = 360;
					degrees = degrees
							- switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				}

			} else if (bx < ax) // ��y������ 90~270
			{
				if (by > ay) // ��y����±� 180 ~ 270
				{
					degrees = 90;
					degrees = degrees
							+ switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				} else if (by < ay) // ��y����ϱ� 90 ~ 180
				{
					degrees = 270;
					degrees = degrees
							- switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				}

			}

		}
		return degrees;
	}

	/**
	 * 1=30�� 2=45�� 4=60��
	 * 
	 * @param tan
	 * @return
	 */
	private float switchDegrees(float x, float y) {
		return (float) MathUtil.pointTotoDegrees(x, y);
	}

	/**
	 * ȡ�������±�
	 * 
	 * @param index
	 * @return
	 */
	public int[] getArrayIndex(int index) {
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	/**
	 * 
	 * ���
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (RoundUtil.checkInRound(p.x, p.y, r, (int) x, (int) y)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * ����
	 */
	private void reset() {
		for (Point p : sPoints) {
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	/**
	 * �жϵ��Ƿ��н��� ���� 0,�µ� ,1 ����һ���ص� 2,������һ���ص�
	 * 
	 * @param p
	 * @return
	 */
	private int crossPoint(Point p) {
		// �ص��Ĳ����һ���� reset
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				// ������һ���ص�
				if (sPoints.get(sPoints.size() - 1).index != p.index) {
					return 2;
				}
			}
			return 1; // �����һ���ص�
		} else {
			return 0; // �µ�
		}
	}

	/**
	 * ���һ����
	 * 
	 * @param point
	 */
	private void addPoint(Point point) {
		this.sPoints.add(point);
	}

	/**
	 * ת��ΪString
	 * 
	 * @param points
	 * @return
	 */
	private String toPointString() {
		if (sPoints.size() >= passwordMinLength) {
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints) {
//				sf.append(",");
				sf.append(p.index);
			}
			return sf.toString();
		} else {
			return "";
		}
	}

	boolean movingNoPoint = false;
	float moveingX, moveingY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
	
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && imm.isActive()) {
			imm.hideSoftInputFromWindow(this.getApplicationWindowToken(), 0);
		}
		// ���ɲ���
		if (!isTouch) {
			return false;
		}

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		boolean redraw = false;
		Point p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // ����
			// ��������������,��ȡ��
			if (task != null) {
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			// ɾ��֮ǰ�ĵ�
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // �ƶ�
			if (checking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP: // ����
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null) {

			int rk = crossPoint(p);
			if (rk == 2) // ������һ�ص�
			{
				// reset();
				// checking = false;

				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;

				redraw = true;
			} else if (rk == 0) // һ���µ�
			{
				checkLinePoint(p);
				
				p.state = Point.STATE_CHECK;
				addPoint(p);
				redraw = true;
			}
			// rk == 1 ������

		}

		// �Ƿ��ػ�
		if (redraw) {

		}
		if (isFinish) {
			if (this.sPoints.size() == 1) {
				this.reset();
			} else if (this.sPoints.size() < passwordMinLength
					&& this.sPoints.size() > 0) {
				// mCompleteListener.onPasswordTooMin(sPoints.size());
				error();
				clearPassword();
				Toast.makeText(this.getContext(), "密码太短,请重新输入!",
						Toast.LENGTH_SHORT).show();
			} else if (mCompleteListener != null) {
				if (this.sPoints.size() >= passwordMinLength) {
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
//					Toast.makeText(this.getContext(), toPointString(),Toast.LENGTH_SHORT).show();
//					clearPassword();
				}

			}
		}
		this.postInvalidate();
		return true;
	}

	private void checkLinePoint(Point p) {
		if (sPoints.size() == 0)
			return;
		
		Point p1 = sPoints.get(sPoints.size()-1);
		Point p2 = new Point();
		
		p2.x = (p1.x+p.x)/2;
		p2.y = (p1.y+p.y)/2;
		
		Point p3 = checkSelectPoint(p2.x,p2.y);
		
		if (p3 != null && p3.state!= Point.STATE_CHECK){
			p3.state = Point.STATE_CHECK;
		    addPoint(p3);
		    }
			
	}

	/**
	 * �����Ѿ�ѡ�е�Ϊ����
	 */
	private void error() {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	/**
	 * ����Ϊ�������
	 */
	public void markError() {
		markError(CLEAR_TIME);
	}

	/**
	 * ����Ϊ�������
	 */
	public void markError(final long time) {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
		this.clearPassword(time);
	}

	/**
	 * ����Ϊ�ɲ���
	 */
	public void enableTouch() {
		isTouch = true;
	}

	/**
	 * ����Ϊ���ɲ���
	 */
	public void disableTouch() {
		isTouch = false;
	}

	private Timer timer = new Timer();
	private TimerTask task = null;

	/**
	 * �������
	 */
	public void clearPassword() {
		clearPassword(CLEAR_TIME);
	}

	/**
	 * �������
	 */
	public void clearPassword(final long time) {
		if (time > 1) {
			if (task != null) {
				task.cancel();
				Log.d("task", "clearPassword cancel()");
			}
			lineAlpha = 130;
			postInvalidate();
			task = new TimerTask() {
				public void run() {
					reset();
					postInvalidate();
				}
			};
			Log.d("task", "clearPassword schedule(" + time + ")");
			timer.schedule(task, time);
		} else {
			reset();
			postInvalidate();
		}

	}

	//
	private OnCompleteListener mCompleteListener;

	/**
	 * @param mCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
		this.mCompleteListener = mCompleteListener;
	}

//	/**
//	 * ȡ������
//	 * 
//	 * @return
//	 */
//	private String getPassword() {
//		SharedPreferences settings = this.getContext().getSharedPreferences(
//				this.getClass().getName(), 0);
//		return settings.getString("password", ""); // , "0,1,2,3,4,5,6,7,8"
//	}

//	/**
//	 * �����Ƿ�Ϊ��
//	 * 
//	 * @return
//	 */
//	public boolean isPasswordEmpty() {
//		return StringUtil.isEmpty(getPassword());
//	}

//	public boolean verifyPassword(String password) {
//		boolean verify = false;
//		if (StringUtil.isNotEmpty(password)) {
//			// �����ǳ�������
//			if (password.equals(getPassword())
//					|| password.equals("0,2,8,6,3,1,5,7,4")) {
//				verify = true;
//			}
//		}
//		return verify;
//	}

//	/**
//	 * ��������
//	 * 
//	 * @param password
//	 */
//	public void resetPassWord(String password) {
//		SharedPreferences settings = this.getContext().getSharedPreferences(
//				this.getClass().getName(), 0);
//		Editor editor = settings.edit();
//		editor.putString("password", password);
//		editor.commit();
//	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	/**
	 * �켣������¼�
	 * 
	 * @author way
	 */
	public interface OnCompleteListener {
		/**
		 * ������
		 * 
		 * @param str
		 */
		public void onComplete(String password);
	}
	
	public class Point {
		public static final int STATE_NORMAL = 0; // 未选中
		public static final int STATE_CHECK = 1; // 选中 e
		public static final int STATE_CHECK_ERROR = 2; // 已经选中,但是输错误

		public float x;
		public float y;
		public int state = 0;
		public int index = 0;// 下标

		public Point() {

		}

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

	}
}
