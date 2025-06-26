package com.infisense.usbir.view;//package com.infisense.usbir.view;
//
//import android.content.Context;
//import android.graphics.drawable.ColorDrawable;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.infisense.usbir.R;
//import com.infisense.usbir.adapter.ImgAdapter;
//
//public class PopuImgMenu {
//
//    private PopupWindow popupWindow;
//    private RecyclerView recyclerView;
//
//    public PopuImgMenu(Context context, ImgAdapter imgAdapter) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_filter, null);
//        popupWindow = new PopupWindow(view);
//        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(false);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000)); // 解决 7.0 手机，点击外部不消失
//        popupWindow.setAnimationStyle(R.style.contextMenuAnim);
//        recyclerView = view.findViewById(R.id.recycler_view);
//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//
//        //创建布局管理
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(layoutManager);
//        //popupWindow.setOnDismissListener(listener);
//        recyclerView.setAdapter(imgAdapter);
//
//    }
//
//    public void showheight(LinearLayout linearLayout, int popupheight) {
//        popupWindow.showAtLocation(linearLayout, Gravity.NO_GRAVITY, 0, popupheight);
//    }
//}
//
//
