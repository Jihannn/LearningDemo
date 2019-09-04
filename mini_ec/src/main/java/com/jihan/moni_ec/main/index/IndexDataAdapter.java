package com.jihan.moni_ec.main.index;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.jihan.mini_core.app.Mini;
import com.jihan.mini_core.delegates.MiniDelegate;
import com.jihan.mini_core.delegates.web.WebDelegateImpl;
import com.jihan.mini_core.net.RestClient;
import com.jihan.mini_core.net.callback.IError;
import com.jihan.mini_core.net.callback.ISuccess;
import com.jihan.moni_ec.R;
import com.jihan.moni_ec.configs.ApiConfigs;
import com.jihan.moni_ec.main.index.banner.BannerCreators;
import com.jihan.moni_ec.main.index.data.DataEntity;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;

/**
 * @author Jihan
 * @date 2019/8/27
 */
public class IndexDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener {

    private static final int VIEW_EMPTY = 1;
    private static final int VIEW_BANNER = 2;
    private static final int VIEW_ARTICLE = 3;
    private static final int VIEW_LOAD_MORE = 4;

    private final ArrayList<DataEntity> DATAS = new ArrayList<>();
    private final ArrayList<DataEntity> BANNERS = new ArrayList<>();
    private MiniDelegate DELEGATE = null;

    private int mBannerCount = 0;
    private boolean isBanner = false;
    private boolean isLoadMore = true;

    public IndexDataAdapter(MiniDelegate delegate) {
        this.DELEGATE = delegate;
    }

    public void addData(ArrayList<DataEntity> data) {
        DATAS.addAll(data);
    }

    public void addBannerData(ArrayList<DataEntity> data) {
        BANNERS.addAll(data);
        mBannerCount += data.size();
    }

    public void clearBanner(){
        BANNERS.clear();
    }

    public void clearData(){
        DATAS.clear();
        isBanner = false;
        isLoadMore = true;
    }

    public void setLoadMore(boolean isFull) {
        isLoadMore = isFull;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && BANNERS.size() > 0) {
            return VIEW_BANNER;
        }
        if (position == DATAS.size() && isLoadMore) {
            return VIEW_LOAD_MORE;
        }
        return VIEW_ARTICLE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = null;

        if (viewType == VIEW_BANNER) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_banner, viewGroup, false);
            return new BannerHolder(view);
        } else if (viewType == VIEW_ARTICLE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_article, viewGroup, false);
            return new DataHolder(view);
        } else if (viewType == VIEW_LOAD_MORE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_load_more, viewGroup, false);
            return new LoadMoreHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof BannerHolder && !isBanner) {
//            Mini.showToast("banner : " + position);
            ConvenientBanner convenientBanner = ((BannerHolder) viewHolder).convenientBanner;
            final ArrayList<String> bannersImages = new ArrayList<>();
            for (int i = 0; i < BANNERS.size(); i++) {
                bannersImages.add(BANNERS.get(i).getBannerImagePath());
            }
            BannerCreators.setDefault(convenientBanner, bannersImages, this);
            isBanner = true;
        }
        if (viewHolder instanceof DataHolder) {
            final int realPosition = isBanner ? position - 1 : position;
            final DataEntity data = DATAS.get(realPosition);
            final DataHolder holder = (DataHolder) viewHolder;
            final int id = DATAS.get(realPosition).getId();
            final boolean[] isCollect = {DATAS.get(realPosition).isCollect()};
//            holder.mIcAuthorIcon.setText();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DELEGATE != null) {
                        DELEGATE.getParentDelegate().getSupportDelegate().start(IndexWebDelegate.newIntent(data.getLink()));
                    }
                }
            });
            holder.mIcHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isCollect[0]){
                        RestClient.builder()
                                .url(ApiConfigs.COLLECTION+id+"/json")
                                .success(new ISuccess() {
                                    @Override
                                    public void success(String response) {
                                        isCollect[0] = true;
                                        holder.mIcHeart.setTextColor(Color.RED);
                                        Mini.showToast("收藏成功");
                                    }
                                })
                                .error(new IError() {
                                    @Override
                                    public void error(int code, String msg) {
                                        Mini.showToast("code : "+code+"\nmsg : "+msg);
                                    }
                                })
                                .build()
                                .post();
                    }else{
                        RestClient.builder()
                                .url(ApiConfigs.UN_COLLECTION+id+"/json")
                                .success(new ISuccess() {
                                    @Override
                                    public void success(String response) {
                                        isCollect[0] = false;
                                        holder.mIcHeart.setTextColor(Color.GRAY);
                                        Mini.showToast("取消收藏");
                                    }
                                })
                                .error(new IError() {
                                    @Override
                                    public void error(int code, String msg) {
                                        Mini.showToast("code : "+code+"\nmsg : "+msg);
                                    }
                                })
                                .build()
                                .post();
                    }
                }
            });
            holder.mTvAuthorName.setText(data.getAuthor());
            holder.mTvParentGroup.setText(data.getSuperChapterName());
            holder.mTvChildGroup.setText(data.getChapterName());
            holder.mTvTitle.setText(data.getTitle());
            holder.mTvDate.setText(data.getNiceDate());
            if(isCollect[0]){
                holder.mIcHeart.setTextColor(Color.RED);
            }else{
                holder.mIcHeart.setTextColor(Color.GRAY);
            }
        }
        if (viewHolder instanceof LoadMoreHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return DATAS.size() + 1;
    }

    @Override
    public void onItemClick(int position) {

    }

    public static class DataHolder extends RecyclerView.ViewHolder {
        public IconTextView mIcAuthorIcon;
        public TextView mTvAuthorName;
        public TextView mTvTitle;
        public IconTextView mIcHeart;
        public TextView mTvParentGroup;
        public TextView mTvChildGroup;
        public TextView mTvDate;

        public DataHolder(@NonNull View itemView) {
            super(itemView);
            mIcAuthorIcon = itemView.findViewById(R.id.icon_author_icon);
            mTvAuthorName = itemView.findViewById(R.id.tv_author_name);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mIcHeart = itemView.findViewById(R.id.icon_heart);
            mTvParentGroup = itemView.findViewById(R.id.tv_parent_group);
            mTvChildGroup = itemView.findViewById(R.id.tv_child_group);
            mTvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    public static class BannerHolder extends RecyclerView.ViewHolder {
        ConvenientBanner convenientBanner;

        public BannerHolder(@NonNull View itemView) {
            super(itemView);
            convenientBanner = itemView.findViewById(R.id.banner_recycler_item);
        }
    }

    public static class LoadMoreHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadMoreHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pb_load_more);
        }
    }
}