package yellow7918.ajou.ac.aunager.routine;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import yellow7918.ajou.ac.aunager.R;


public class RoutineDialogFragment extends DialogFragment {
    private OnRoutineDialogEventListener listener;
    private Routine routine;

    public interface OnRoutineDialogEventListener {
        void onConfirm(Routine routine);
    }

    public void setOnRoutineDialogEventListener(OnRoutineDialogEventListener listener) {
        this.listener = listener;
    }

    private EditText hourEditText;
    private EditText minEditText;

    private EditText doingEditText;

    private String userName;
    private int getHour;
    private int getMinute;

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private Uri photoUri;
    private ImageView photoView;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private static final int MULTIPLE_PERMISSIONS = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_routine, container, false);

        hourEditText = view.findViewById(R.id.time_hour_edit_text);
        Calendar c = Calendar.getInstance();
        getHour = c.get(Calendar.HOUR_OF_DAY);

        photoView = view.findViewById(R.id.image_view_trigger);
        photoView.setOnClickListener(v -> {
            checkPermissions();
            setPopupMenu();
        });

        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        hourEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getHour = Integer.parseInt(charSequence.toString());
                else
                    getHour = -1;

                if (getHour > 23) {
                    getHour = 23;
                    hourEditText.setText("" + getHour);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        minEditText = view.findViewById(R.id.time_min_edit_text);
        getMinute = c.get(Calendar.MINUTE);
        minEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    getMinute = Integer.parseInt(charSequence.toString());
                else
                    getMinute = -1;

                if (getMinute > 59) {
                    getMinute = 59;
                    minEditText.setText("" + getMinute);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        doingEditText = view.findViewById(R.id.text_input_doing);

        if (routine != null) {
//            for (int i = 1; i < routine.getDay().size(); i++) {
//                if (routine.getDay().get(i))
//                    dayCheckBox[i - 1].setChecked(true);
//            }

            Glide.with(photoView)
                    .load(routine.getProfileImageUrl())
                    .apply(new RequestOptions()
                            .override(150, 150)
                            .placeholder(R.drawable.ic_plus)
                    )
                    .into(photoView);

            String[] time = routine.getTime().split(":");
            doingEditText.setText(routine.getDoing());
            getHour = Integer.parseInt(time[0]);
            getMinute = Integer.parseInt(time[1]);
        }

        hourEditText.setText("" + getHour);
        minEditText.setText("" + getMinute);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        Button confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            if (checkError())
                addNewAlarm();
        });

        return view;
    }

    private void addNewAlarm() {
        int id;
        String time = String.format(Locale.getDefault(), "%02d:%02d", getHour, getMinute);
        String doing = doingEditText.getText().toString();

        if (routine != null) {
            id = routine.getRoutineId();
        } else {
            id = (FirebaseAuth.getInstance().getCurrentUser().getUid() + time + userName).hashCode();
        }

        if (listener != null) {

            Routine routine = new Routine(userName, time, doing, true, id, "", FirebaseAuth.getInstance().getCurrentUser().getUid());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl("gs://atedu2018.appspot.com").child("profileImages");

            if (photoUri != null) {

                UploadTask uploadTask = storageReference.child(generateTempFilename()).putFile(photoUri);
                uploadTask.addOnFailureListener(exception -> {

                }).addOnSuccessListener(taskSnapshot -> {
                    String url = taskSnapshot.getUploadSessionUri().toString();
                    routine.setProfileImageUrl(url);
                    listener.onConfirm(routine);
                });
            }
            listener.onConfirm(routine);
        }

        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private boolean checkError() {
        if (getHour > 23) {
            hourEditText.requestFocus();
            Snackbar.make(getView(), "Under 23", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getHour == -1) {
            hourEditText.requestFocus();
            Snackbar.make(getView(), "Write Hour", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getMinute > 59) {
            minEditText.requestFocus();
            Snackbar.make(getView(), "Under 59", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (getMinute == -1) {
            minEditText.requestFocus();
            Snackbar.make(getView(), "Write Minute", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
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
                System.out.println("pick from camera");
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

    private String generateTempFilename() {
        return UUID.randomUUID().toString();
    }

}
