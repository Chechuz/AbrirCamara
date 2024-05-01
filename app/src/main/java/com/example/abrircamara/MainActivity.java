package com.example.abrircamara;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity{
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

        btnCamara.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (validarPermisos())
                    tomarFotografia();
            }
        });
    }
    public boolean validarPermisos() {
        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)){
            Log.i("validaP()", "1-CAMARA");
            return true;
            } else{
                requestPermissions(new String[]{CAMERA},100);
                Log.i("validaP()", "2-Pide permiso ");
            }
        if(shouldShowRequestPermissionRationale(CAMERA)){
            cargarDialogoRecomendacion();
            Log.i("validaP()", "3- Si niega antes, carga dialogo R");
            }
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
                requestPermissions(new String[]{CAMERA}, 100);
                Log.i("acepta", "aceptado");
            }
        });
        dialogo.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (permissions.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

