package com.kpz.AnyChat.Others;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.kpz.AnyChat.Group_Chat.ResultModel;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class SearchListAdapter extends BaseAdapter {
    private Context context;
    private List<BaseInfoBean> SearchList = new ArrayList<>();
    private List<ResultModel> APISearchList = new ArrayList<>();

    String url;
    public SearchListAdapter(Context context, List<ResultModel> APISearchList) {
        this.context = context;

//        this.SearchList = SearchList;
        this.APISearchList = APISearchList;
    }

    @Override
    public int getCount() {
        if (APISearchList == null){
            Log.e("APISearchList", "0");
            return 0;}
        else
            Log.e("APISearchList Size", APISearchList.size()+"");
            return APISearchList.size();

    }

    @Override
    public Object getItem(int position) {
        Log.e("Items", APISearchList.get(position)+"");
        return APISearchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.vim_item_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mIvHead = (ImageView) convertView.findViewById(R.id.icon);
        holder.mTextView = (TextView) convertView.findViewById(R.id.name_tv);

//        long id = SearchList.get(position).getID();

        try {
            long id = Long.parseLong(APISearchList.get(position).groupid);
            RequestHelper.getGroupInfo(id, new RequestCallBack<Group, Void, Void>() {
                @Override
                public void handleSuccess(Group group, Void aVoid, Void aVoid2) {
                    url = group.getAvatar();
                    Log.e("Search AvatarURL",url);
                }
            });
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }



        ResultModel bean = APISearchList.get(position);
        holder.mTextView.setText(bean.name);
        Utils.loadHead(context,url,holder.mIvHead, R.mipmap.vim_icon_default_group );
//        ImageUtil.loadViewLocalHead(context, bean.getIcon(), holder.mIvHead, R.mipmap.vim_icon_default_user);
        return convertView;
    }

    static class ViewHolder {
        ImageView mIvHead;
        TextView mTextView;

        ViewHolder(View view) {

        }
    }
}