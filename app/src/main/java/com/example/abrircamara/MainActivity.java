package com.example.abrircamara;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.CollationKey;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnCamara;
    String rutaImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCamara = findViewById(R.id.btn_camara);

        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarPermisos()){
                    tomarFotografia();
                }else{
                    Toast.makeText(MainActivity.this, "DEBE DAR LOS PERMISOS", Toast.LENGTH_LONG).show();
                    cargarDialogoRecomendacion();
                }
            }
        });

    }

    public boolean validarPermisos() {
        if(Build.VERSION.PREVIEW_SDK_INT<Build.VERSION_CODES.M){
            return  true;
        }
        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }
        Toast.makeText(MainActivity.this, "DEBE DAR LOS PERMISOS", Toast.LENGTH_LONG).show();
        return false;
    }


    private void cargarDialogoRecomendacion() {
        Log.i("dialogo", "entra en cargarDialogoRecom");
        AlertDialog.Builder dialogo = new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA}, 100);
            }
        });
        dialogo.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                tomarFotografia();
            }
        }
    }

    private void tomarFotografia() {
        Log.i("accede al metodo tomar Fotografia", "si accede");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File imgArchivo = null;
            try {
                imgArchivo = crearImagen();
            }catch (IOException ex){
                Log.e("ERROR", ex.toString());
            }

            if(imgArchivo!=null){
                Uri fotoUri = FileProvider.getUriForFile(this, "com.example.abrircamara.fileprovider", imgArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                startActivityForResult(intent, 10);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Intent iGaleria = new Intent(MainActivity.this, GaleriaActivity.class);
            Bundle extras = new Bundle();
            extras.putString("image_path", rutaImagen);
            iGaleria.putExtras(extras);
            startActivity(iGaleria);
        }
    }

    private File crearImagen() throws IOException {
        String nombreImg = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(nombreImg, ".jpg", directorio);

        rutaImagen = imagen.getAbsolutePath();
        return imagen;
    }
}

