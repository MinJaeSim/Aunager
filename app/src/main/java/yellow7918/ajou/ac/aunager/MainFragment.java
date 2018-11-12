package yellow7918.ajou.ac.aunager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import yellow7918.ajou.ac.aunager.login.SignInActivity;
import yellow7918.ajou.ac.aunager.routine.Routine;

public class MainFragment extends Fragment {

    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA}; //권한 설정 변수

    private Uri photoUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private static final int MULTIPLE_PERMISSIONS = 101;

    private String url = "";

    private ProgressDialog progressDialog;

    private Date resultDate;
    private String temp;
    private CircleImageView imageView;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(getContext());
        showProgressDialog("잠시만 기다려 주세요");


        imageView = view.findViewById(R.id.profile_image_view);
        db.collection("User")
                .document(user.getEmail())
                .get().addOnSuccessListener(documentSnapshot -> {
            User u = documentSnapshot.toObject(User.class);
            if (u == null)
                return;
            Glide.with(imageView)
                    .load(u.getProfileImage())
                    .apply(new RequestOptions()
                            .centerInside()
                            .placeholder(R.drawable.ic_camera)
                    )
                    .into(imageView);
            dismissProgressDialog();
        });

        imageView.setOnClickListener(v -> {
            checkPermissions();
            setPopupMenu();
        });

        TextView title = view.findViewById(R.id.text_title);
        TextView condition = view.findViewById(R.id.condition);
        TextView conditionDetail = view.findViewById(R.id.condition_detail);

        Button toilet = view.findViewById(R.id.toilet);
        Button medicine = view.findViewById(R.id.medicine);

        title.setText(String.format("%s의 어니저", user.getDisplayName()));
        condition.setText(String.format("%s의 오늘 상태", user.getDisplayName()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        resultDate = new Date(System.currentTimeMillis());
        temp = sdf.format(resultDate);
        try {
            Date d = sdf.parse(temp);

            DocumentReference ref = db.collection("Routine").document(user.getEmail() + "." + d.getTime());

            Task<DocumentSnapshot> task = ref.get().addOnSuccessListener(doc -> {
                if (!doc.exists()) {
                    conditionDetail.setText("\n오늘의 상태를 입력해 주세요.");
                    conditionDetail.setTextColor(Color.rgb(240, 10, 10));
                    conditionDetail.setTextSize(30);
                    return;
                }


                Routine r = doc.toObject(Routine.class);

                SimpleDateFormat hourMin = new SimpleDateFormat("hh:mm");
                Date tempDate = new Date(r.getToiletTime());
                String time = hourMin.format(tempDate);

                int score = 0;
                if (r.getWeather().equals("맑음"))
                    score += 3;
                else if (r.getWeather().equals("흐림"))
                    score += 2;
                else if (r.getWeather().equals("눈"))
                    score += 1;
                else if (r.getWeather().equals("비"))
                    score += 0;

                if (r.isToilet())
                    score += 2;

                if (r.isMedicine())
                    score += 1;

                int h = Integer.parseInt(r.getSleepHour().substring(0, 2).trim()) * 60;
                int m = Integer.parseInt(r.getSleepHour().substring(2, 4).trim());

                if (7 * 60 > h + m)
                    score -= 1;
                else
                    score += 1;

                conditionDetail.setText(String.format(Locale.getDefault(), " 오늘 %s는 약 %s시간 %s분 정도 잠을 잤어요." +
                                "\n %s 는 화장실을 %s " +
                                "\n %s 는 약을 %s " +
                                "\n %s 의 전체적인 컨디션은 아마 %s",
                        user.getDisplayName(), r.getSleepHour().substring(0, 2), r.getSleepHour().substring(2, 4),
                        user.getDisplayName(), r.isToilet() ? "갔다왔고, " + time + "에 갔다왔어요." : "아직 갔다오지 않았어요.",
                        user.getDisplayName(), r.isMedicine() ? "먹었어요." : "아직 먹지 않았어요.",
                        user.getDisplayName(), score > 7 ? "좋을 것 같아요." : score > 4 ? "중간 정도 일거에요." : "좋지 않을거에요."));
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        toilet.setOnClickListener(v -> {
            resultDate = new Date(System.currentTimeMillis());
            temp = sdf.format(resultDate);
            try {
                Date d = sdf.parse(temp);
                db.collection("Routine").document(user.getEmail() + "." + d.getTime())
                        .update("toiletTime", System.currentTimeMillis(), "toilet", true)
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Snackbar.make(view, "등록에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "등록에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        medicine.setOnClickListener(v -> {
            resultDate = new Date(System.currentTimeMillis());
            temp = sdf.format(resultDate);
            try {
                Date d = sdf.parse(temp);
                db.collection("Routine").document(user.getEmail() + "." + d.getTime())
                        .update("medicine", true)
                        .addOnCompleteListener(t -> {
                            if (t.isSuccessful()) {
                                Snackbar.make(view, "등록에 성공하였습니다.", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "등록에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private void uploadImages(Uri photoUri, String email) {
        showProgressDialog("잠시만 기다려 주세요");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://atedu2018.appspot.com").child("profile").child(email);
        storageReference.putFile(photoUri).continueWithTask(task -> storageReference.getDownloadUrl()).addOnCompleteListener(task -> {
            url = task.getResult().toString();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("User").document(email)
                    .update("profileImage", url)
                    .addOnCompleteListener(v -> {
                        imageView.setImageURI(photoUri);
                        dismissProgressDialog();
                    });
        });
    }

    private void checkPermissions() {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(getContext(), permission);
            if (result != PackageManager.PERMISSION_GRANTED)  //사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(permission);
        }

        if (!permissionList.isEmpty())
            ActivityCompat.requestPermissions(getActivity(), permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);

    }

    private void setPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), imageView);
        popup.getMenuInflater()
                .inflate(R.menu.camera_popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.popup_take_picture)
                    takePhoto();
                else if (item.getItemId() == R.id.popup_photo_select)
                    goToAlbum();
                return true;
            }
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
            photoUri = FileProvider.
                    getUriForFile(getContext(),
                            "yellow7918.ajou.ac.aunager.provider",
                            photoFile);
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

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/test/"); //test라는 경로에 이미지를 저장하기 위함
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PICK_FROM_ALBUM) {
                if (data == null)
                    return;

                photoUri = data.getData();
                cropImage();
            } else if (requestCode == PICK_FROM_CAMERA) {
                System.out.println("pick from camera");
                cropImage();
                MediaScannerConnection.scanFile(getContext(),
                        new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else if (requestCode == CROP_FROM_CAMERA) {
                try {
                    uploadImages(photoUri, user.getEmail());
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        }
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
            Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
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

            photoUri = FileProvider.getUriForFile(getContext(),
                    "yellow7918.ajou.ac.aunager.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }


            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행

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

    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
