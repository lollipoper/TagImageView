package com.lollipoper.tagimage.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lollipoer.tagimage.R;

/**
 * 标签ImageView
 * 
 * @author lollipoper
 */
public class TAGImageView extends ImageView {
	/**
	 * 图片描述显示位置
	 */
	enum ImageDescripterGravity {
		LEFT_BONTTOM/* 左下角 */, RIGHT_BOTTOM/* 右下角 */
	}

	public static final int DEFAULT_PADDING = 16;
	public static final int TAG_RECT_MIN_HEIGHT = 80;// 标签背景最小宽度
	public static final int DEFAULT_IMAGE_DESCRIPT_PADDING = 50;// 图片描述默认padding

	private int tagDisY;// 标签距y轴直角边长度，单位px
	private int tagDisX;// 标签与x轴交点长度
	private String tagName = "推荐";// 标签名称
	private boolean isShowTag = true;
	private String imageDescript = "";// 图片描述
	private ImageDescripterGravity defaultImageDescriptGravity = ImageDescripterGravity.RIGHT_BOTTOM;
	private int imageDescriptColor;// 图片描述字体颜色
	private int imageDescriptPadding;

	private int tagTextColor;// 标签颜色
	private int tagBGColor;// 标签背景
	private int tagTextSize;// 标签字体大小,单位px
	private Paint tagPaint, tagBGPaint, imageDescriptPaint;
	private Rect tagRectF;// 标签背景区域
	private Typeface tagTypeface;// 标签字体
	private static final int rotateAngle = -45;// 标签旋转角度,之后扩展，暂时只能是45度角

	public TAGImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttr(context, attrs);
		init();
	}

	private void initAttr(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.TAGImageView);
		if (typedArray != null) {
			tagBGColor = typedArray.getColor(
					R.styleable.TAGImageView_tagBackground, Color.RED);
			tagTextColor = typedArray.getColor(
					R.styleable.TAGImageView_tagTextColor, Color.YELLOW);
			tagDisY = typedArray.getDimensionPixelSize(
					R.styleable.TAGImageView_tagPosition, 300);
			String name = typedArray
					.getString(R.styleable.TAGImageView_tagName);
			if (!TextUtils.isEmpty(name)) {
				tagName = name;
			}
			tagTextSize = typedArray.getDimensionPixelOffset(
					R.styleable.TAGImageView_tagTextSize, 50);
			isShowTag = typedArray.getBoolean(
					R.styleable.TAGImageView_tagVisiable, true);

			// image descript attr
			String imageName = typedArray
					.getString(R.styleable.TAGImageView_imageDescriptName);
			if (!TextUtils.isEmpty(imageName)) {
				imageDescript = imageName;
			}
			imageDescriptColor = typedArray.getColor(
					R.styleable.TAGImageView_imageDescriptColor, Color.BLACK);
			int gravity = typedArray.getInt(
					R.styleable.TAGImageView_imageDescriptGravity, 1);
			switch (gravity) {
			case 0:
				defaultImageDescriptGravity = ImageDescripterGravity.LEFT_BONTTOM;
				break;
			case 1:
				defaultImageDescriptGravity = ImageDescripterGravity.RIGHT_BOTTOM;
			default:
				break;
			}

			imageDescriptPadding = typedArray.getDimensionPixelSize(
					R.styleable.TAGImageView_imageDescriptPadding,
					DEFAULT_IMAGE_DESCRIPT_PADDING);
			typedArray.recycle();
		}
	}

	public TAGImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(context, attrs);
		init();
	}

	public TAGImageView(Context context) {
		super(context);
		initAttr(context, null);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 处理标签
		if (isShowTag) {
			drawTAG(canvas);
		}
		drawImageDescript(canvas);
	}

	private void drawImageDescript(Canvas canvas) {
		if (imageDescript == null || imageDescriptPaint == null) {
			return;
		}
		switch (defaultImageDescriptGravity) {
		case LEFT_BONTTOM:
			canvas.drawText(
					imageDescript,
					getWidth()
							- getStringWidth(imageDescriptPaint, imageDescript)
							- imageDescriptPadding, getHeight()
							- imageDescriptPadding, imageDescriptPaint);
			break;
		case RIGHT_BOTTOM:
			canvas.drawText(imageDescript, imageDescriptPadding, getHeight()
					- imageDescriptPadding, imageDescriptPaint);
			break;
		default:
			break;
		}
	}

	private int getStringWidth(Paint paint, String str) {
		Rect bounds = new Rect();
		paint.getTextBounds(str, 0, str.length(), bounds);
		return bounds.width();
	}

	private void drawTAG(Canvas canvas) {
		canvas.save();
		// 判断标签位置是否超过View的宽度
		if (tagDisY > getWidth() && tagDisY < getHeight()) {
			tagDisY = getHeight();
		}
		tagDisX = (int) (Math.tan(Math.abs(rotateAngle) * Math.PI / 180) * tagDisY);
		// 原理先水平绘制标签之后旋转到一定的角度以达到需求效果,在代码中是先旋转后绘制，与实际想法有所牌偏差
		canvas.rotate(rotateAngle, 0, tagDisX);
		// 需要绘制的标签背景宽度，此宽度比屏幕能看到的宽度略宽，在屏幕上显示的效果是与需求一致的
		int tagRectWidth = (int) (Math.sqrt(tagDisX * tagDisX + tagDisY
				* tagDisY));
		int tagBGHeight = TAG_RECT_MIN_HEIGHT + DEFAULT_PADDING * 2;
		if (getTagHeight() > TAG_RECT_MIN_HEIGHT - DEFAULT_PADDING * 2) {
			tagBGHeight = getTagHeight() + DEFAULT_PADDING * 2;
		}
		tagRectF = new Rect(0, tagDisX - tagBGHeight, tagRectWidth, tagDisX);
		canvas.drawRect(tagRectF, tagBGPaint);

		// 绘制标签
		FontMetricsInt fontMetrics = tagPaint.getFontMetricsInt();
		int tagTextY = (tagRectF.bottom + tagRectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
		// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
		tagPaint.setTextAlign(Paint.Align.CENTER);
		float tagTextX = (float) (tagRectWidth / 2);
		// 得到本视图的弦长
		float viewChroidLength = (float) Math.sqrt(getHeight() * getHeight()
				+ getWidth() * getWidth());
		// 如果标签显示位置超过视图弦长的一般，则需要对标签的字体写入位置进行平移，以达到中心效果
		if (tagTextX >= viewChroidLength / 2) {
			tagTextX -= Math.abs(getWidth() - getHeight())
					/ Math.sin(rotateAngle * Math.PI / 180);
		}
		canvas.drawText(tagName, tagTextX, tagTextY, tagPaint);
		canvas.restore();
	}

	private void init() {
		tagPaint = new Paint();
		tagPaint.setAntiAlias(true);
		tagPaint.setTextSize(tagTextSize);
		tagPaint.setColor(tagTextColor);
		if (tagTypeface != null) {
			tagPaint.setTypeface(tagTypeface);
		}
		tagBGPaint = new Paint();
		tagBGPaint.setColor(tagBGColor);

		imageDescriptPaint = new Paint();
		imageDescriptPaint.setAntiAlias(true);
		imageDescriptPaint.setTextSize(tagTextSize);
		imageDescriptPaint.setColor(imageDescriptColor);
		if (tagTypeface != null) {
			imageDescriptPaint.setTypeface(tagTypeface);
		}
	}

	public void setTagDisY(int tagDisY) {
		this.tagDisY = tagDisY;
		invalidate();
	}

	public void setTagName(String tagName) {
		if (tagName == null) {
			return;
		}
		this.tagName = tagName;
		invalidate();
	}

	public void setTagTextColor(int tagTextColor) {
		this.tagTextColor = tagTextColor;
		tagPaint.setColor(tagTextColor);
		invalidate();
	}

	public void setImageDescriptColor(int imageDescriptColor) {
		this.imageDescriptColor = imageDescriptColor;
		imageDescriptPaint.setColor(imageDescriptColor);
		invalidate();
	}

	public void setTagBGColor(int tagBGColor) {
		this.tagBGColor = tagBGColor;
		tagBGPaint.setColor(tagBGColor);
		invalidate();
	}

	public void setTagTextSize(int tagTextSize) {
		this.tagTextSize = tagTextSize;
		tagPaint.setTextSize(tagTextSize);
		imageDescriptPaint.setTextSize(tagTextSize);
		invalidate();
	}

	public void setTagTypeface(Typeface tagTypeface) {
		if (tagTypeface == null) {
			return;
		}
		this.tagTypeface = tagTypeface;
		tagPaint.setTypeface(tagTypeface);
		imageDescriptPaint.setTypeface(tagTypeface);
		invalidate();
	}

	public void setShowTag(boolean isShowTag) {
		this.isShowTag = isShowTag;
		invalidate();
	}

	public void setImageDescript(String imageDescript) {
		this.imageDescript = imageDescript;
		invalidate();
	}

	public void setDefaultImageDescriptGravity(
			ImageDescripterGravity defaultImageDescriptGravity) {
		this.defaultImageDescriptGravity = defaultImageDescriptGravity;
		invalidate();
	}

	public void setImageDescriptPadding(int imageDescriptPadding) {
		this.imageDescriptPadding = imageDescriptPadding;
		invalidate();
	}

	// 得到标签字体高度
	private int getTagHeight() {
		Rect bounds = new Rect();
		tagPaint.getTextBounds(tagName, 0, tagName.length(), bounds);
		return bounds.height();
	}
}
