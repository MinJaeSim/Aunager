package yellow7918.ajou.ac.aunager.diet;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import yellow7918.ajou.ac.aunager.R;

public class DietDialogFragment extends DialogFragment {
    private DietDialogFragment.OnDietDialogEventListener listener;

    public interface OnDietDialogEventListener {
        void onConfirm(Diet diet);
    }

    public void setOnDietEventListener(DietDialogFragment.OnDietDialogEventListener listener) {
        this.listener = listener;
    }

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private Diet diet;

    private Uri photoUri;
    private ImageView photoView;
    private RadioButton breakfast;
    private RadioButton lunch;
    private RadioButton dinner;

    private RadioButton mon;
    private RadioButton tue;
    private RadioButton wed;
    private RadioButton thu;
    private RadioButton fri;
    private RadioButton sat;
    private RadioButton sun;

    private ArrayList<RadioButton> days;


    private Button confirmButton;
    private Button cancelButton;

    private EditText mealText;
    private EditText timeText;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private static final int MULTIPLE_PERMISSIONS = 101; //권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_diet, container, false);
        photoView = view.findViewById(R.id.image_view_trigger);
        photoView.setOnClickListener(v -> {
            checkPermissions();
            setPopupMenu();
        });

        breakfast = view.findViewById(R.id.radio_breakfast);
        lunch = view.findViewById(R.id.radio_lunch);
        dinner = view.findViewById(R.id.radio_dinner);

        mealText = view.findViewById(R.id.meal_edit_text);
        timeText = view.findViewById(R.id.time_edit_text);

        confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            if (checkError())
                addNewDiet();
        });

        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        sat = view.findViewById(R.id.sat);
        sun = view.findViewById(R.id.sun);

        days = new ArrayList<>();
        days.add(mon);
        days.add(tue);
        days.add(wed);
        days.add(thu);
        days.add(fri);
        days.add(sat);
        days.add(sun);

        if (diet != null) {
            days.get(diet.getDay()).setChecked(true);
            mealText.setText(diet.getMeal());
            timeText.setText(diet.getDate());

            Glide.with(photoView)
                    .load(diet.getProfileImageUrl())
                    .apply(new RequestOptions()
                            .override(150, 150)
                            .placeholder(R.drawable.ic_plus)
                    )
                    .into(photoView);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PICK_FROM_ALBUM) {
                if (data == null)
                    return;

                photoUri = data.getData();
                photoView.setImageURI(photoUri);
                photoView.setVisibility(View.GONE);
                cropImage();
            } else if (requestCode == PICK_FROM_CAMERA) {
                cropImage();
                MediaScannerConnection.scanFile(getActivity(),
                        new String[]{photoUri.getPath()}, null,
                        (path, uri) -> {
                        });
            } else if (requestCode == CROP_FROM_CAMERA) {
                try {
                    photoView.setImageURI(photoUri);
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }
    }

    private void checkPermissions() {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result != PackageManager.PERMISSION_GRANTED)  //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(permission);
        }

        if (!permissionList.isEmpty())
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);

    }

    private void setPopupMenu() {
        PopupMenu popup = new PopupMenu(getActivity(), photoView);
        popup.getMenuInflater()
                .inflate(R.menu.camera_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.popup_take_picture)
                takePhoto();
            else if (item.getItemId() == R.id.popup_photo_select)
                goToAlbum();
            return true;
        });
        popup.show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getActivity(),
                    "ac.ajou.simminje.ateducom.provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public void cropImage() {
        System.out.println("crop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getActivity().grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            getActivity().grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


        int size = list.size();
        if (size == 0) {
            Toast.makeText(getActivity(), "cancel", Toast.LENGTH_SHORT).show();
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/test/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(getActivity(),
                    "ac.ajou.simminje.ateducom.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                getActivity().grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

    private void addNewDiet() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());
//
//        String date = simpleDateFormat.format(System.currentTimeMillis());

        // mon == 0;
        int day = 0;
        for (RadioButton r : days) {
            if (r.isChecked()) {
                break;
            }
            day++;
        }

        int type = breakfast.isChecked() ? 1 : lunch.isChecked() ? 2 : 3;
        Diet diet = new Diet(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), type, timeText.getText().toString(), mealText.getText().toString(), day, "", FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (listener != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://atedu2018.appspot.com").child("dietImages");

            if (diet.getProfileImageUrl() != null) {

                UploadTask uploadTask = storageReference.child(generateTempFilename()).putFile(photoUri);
                uploadTask.addOnFailureListener(exception -> {

                }).addOnSuccessListener(taskSnapshot -> {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    diet.setProfileImageUrl(url);
                    listener.onConfirm(diet);
                });
            }
        }

        dismiss();
    }

    public void setDiet(Diet diet) {
        this.diet = diet;
    }

    private boolean checkError() {
        if (!breakfast.isChecked() && !lunch.isChecked() && !dinner.isChecked())
            return false;
        return true;
    }

    private String generateTempFilename() {
        return UUID.randomUUID().toString();
    }
}
