package com.platfomni.ymkclusterization;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.map.CameraPosition;

public class MapView extends com.yandex.mapkit.mapview.MapView {

    private static final float ZOOM_DURATION = 0.3f;
    private static final float USER_ZOOM_DURATION = 2.0f;

    public MapView(Context context) {
        super(context);
        initView(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrset) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View mapView = inflater.inflate(R.layout.map_view, this);

        TypedArray ta = getContext().obtainStyledAttributes(attrset, R.styleable.MapView);

        int controlButtonSize = (int) ta.getDimension(R.styleable.MapView_control_button_size, 50);
        int controlsPosition = ta.getInt(R.styleable.MapView_controls_position, 4);
        int controlsPadding = (int) ta.getDimension(R.styleable.MapView_controls_padding, dpToPx(16, context));

        int plusImg = ta.getResourceId(R.styleable.MapView_img_plus, R.drawable.ic_plus);
        int minusImg = ta.getResourceId(R.styleable.MapView_img_minus, R.drawable.ic_minus);
        int locationImg = ta.getResourceId(R.styleable.MapView_img_location, R.drawable.ic_place);

        ta.recycle();


        ImageView plus = mapView.findViewById(R.id.plus);
        ImageView minus = mapView.findViewById(R.id.minus);
        ImageView userLocation = mapView.findViewById(R.id.user_location);
        LinearLayout controlsLayout = mapView.findViewById(R.id.controls_layout);

        plus.getLayoutParams().height = controlButtonSize;
        plus.getLayoutParams().width = controlButtonSize;
        plus.setImageResource(plusImg);

        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getMap().move(
                        new CameraPosition(getMap().getCameraPosition().getTarget(), getMap().getCameraPosition().getZoom() + 1f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, ZOOM_DURATION),
                        null);
            }
        });

        minus.getLayoutParams().height = controlButtonSize;
        minus.getLayoutParams().width = controlButtonSize;
        minus.setImageResource(minusImg);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { //Уменьшаем масштаб
                getMap().move(
                        new CameraPosition(getMap().getCameraPosition().getTarget(), getMap().getCameraPosition().getZoom() - 1f, 0.0f, 0.0f),
                        new Animation(Animation.Type.SMOOTH, ZOOM_DURATION),
                        null);
            }
        });

        userLocation.getLayoutParams().height = controlButtonSize;
        userLocation.getLayoutParams().width = controlButtonSize;
        userLocation.setImageResource(locationImg);

        userLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { //Центрируем пользователя
                if (getMap().getUserLocationLayer().cameraPosition() != null) {
                    getMap().move(
                            new CameraPosition(getMap().getUserLocationLayer().cameraPosition().getTarget(), 15, 0.0f, 0.0f),
                            new Animation(Animation.Type.SMOOTH, USER_ZOOM_DURATION),
                            null);
                }
            }
        });


        controlsLayout.setPadding(controlsPadding, controlsPadding, controlsPadding, controlsPadding);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) controlsLayout.getLayoutParams();
        switch (controlsPosition) {
            case 1:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case 2:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 3:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                break;
            case 4:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
        }
        controlsLayout.setLayoutParams(params);
    }

    public static float dpToPx(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
