package com.example.yandexclussterization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.platfomni.ymkclusterization.ClusterManager;
import com.platfomni.ymkclusterization.MapView;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.logo.Alignment;
import com.yandex.mapkit.logo.HorizontalAlignment;
import com.yandex.mapkit.logo.VerticalAlignment;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements InputListener {

    private static final float ZOOM_DURATION = 0.3f;
    private static final float USER_ZOOM_DURATION = 2.0f;

    @BindView(R.id.mapview)
    private MapView mapView;

    private ClusterManager<MarkerItem> clusterManager;
    private ClusterIconRenderer clusterIconRenderer;
    private MarkerItem selectedItem;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapView.getMap().getUserLocationLayer().setEnabled(true);
                } else {
                    mapView.getMap().getUserLocationLayer().setEnabled(false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey("4907418a-68a0-4dc0-af4a-c7f4918c922a");
        MapKitFactory.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);

        Point point = new Point(58.008765, 56.226545);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            mapView.getMap().getUserLocationLayer().setEnabled(true);
        }

        mapView.getMap().move(
                new CameraPosition(point, 12.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, ZOOM_DURATION),
                null);

        mapView.getMap().getLogo().setAlignment(new Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)); //Переместить лого в левый нижний угол


        clusterManager = new ClusterManager<>(this, mapView); //кластер-менеджер (отвечает за группировку маркеров, обботку нажатий на маркер/кластер)
        clusterIconRenderer = new ClusterIconRenderer(this, mapView, clusterManager); //кластер-рендер (отвечает за отрисовку маркеров/кластеров)
        clusterManager.setRenderer(clusterIconRenderer);

        mapView.getMap().addInputListener(this); //Перехватываем нажатия по карте (для снятия выделения)

        //тыкаем по маркеру
        clusterManager.setOnMarkerClickListener((ClusterManager.OnMarkerClickListener<MarkerItem>) markerItem -> {
            if (selectedItem != null) {
                selectedItem.setSelected(false); //Выбранный маркер делаем "не выбранным"
                clusterIconRenderer.updateClusterItem(selectedItem); //обновляем картинку у маркера
            }

            selectedItem = markerItem; //новый выбранный маркер ...
            markerItem.setSelected(true); //... становится выбранным
            clusterIconRenderer.updateClusterItem(markerItem); //обновляем картинку у маркера
        });

        //clusterManager.setOnClusterClickListener(ClusterManager.OnClusterClickListener) - тыкаем по кластеру (в менеджере есть реализация по умолчанию)
        clusterManager.addItems(mockStores());
        clusterManager.cluster();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_refresh:
                clusterManager.clearItems();
                clusterManager.addItems(mockStores());
                clusterManager.cluster();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onMapTap(@NonNull Map map, @NonNull Point point) {
        clearSelection();
    }

    @Override
    public void onMapLongTap(@NonNull Map map, @NonNull Point point) {

    }

    private void clearSelection() {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
            clusterIconRenderer.updateClusterItem(selectedItem);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    public List<MarkerItem> mockStores() {
        List<MarkerItem> markers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            markers.add(new MarkerItem(new Store(57.5 + random.nextDouble(), 55.5 + random.nextDouble() * 2, random.nextInt())));
        }

        //markers.add(new MarkerItem(new Store(57.5 + random.nextDouble(), 55.5 + random.nextDouble() * 2, -1)));
        return markers;
    }
}
