package com.mebooth.mylibrary.main.utils;

import com.mebooth.mylibrary.main.AppApplication;
import com.mebooth.mylibrary.main.home.bean.CityListJson;
import com.mebooth.mylibrary.main.home.bean.CommentOnJson;
import com.mebooth.mylibrary.main.home.bean.CustomizeJson;
import com.mebooth.mylibrary.main.home.bean.EntranceJson;
import com.mebooth.mylibrary.main.home.bean.FlushJson;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetIMUserInfoJson;
import com.mebooth.mylibrary.main.home.bean.GetIsCollectJson;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetMeCollectJson;
import com.mebooth.mylibrary.main.home.bean.GetMineCountJson;
import com.mebooth.mylibrary.main.home.bean.GetMyUserInfo;
import com.mebooth.mylibrary.main.home.bean.GetNewInfoJson;
import com.mebooth.mylibrary.main.home.bean.GetNowDetailsJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.GetRongIMTokenJson;
import com.mebooth.mylibrary.main.home.bean.GetShareInfoJson;
import com.mebooth.mylibrary.main.home.bean.PlacesInfoJson;
import com.mebooth.mylibrary.main.home.bean.ProvincesListJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;
import com.mebooth.mylibrary.main.home.bean.UserNewsListJson;
import com.mebooth.mylibrary.utils.DateUtils;

import java.util.Date;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface YService {
    //测试
//    String BASE_URL = "http://test-tataclub.baojiawangluo.com/";
    public static String BASE_URL = AppApplication.baseUrl();
    //外网s
//    String BASE_URL = "http://test.tatabike.com/";
//    String BASE_URL = "http://tataclub.baojiawangluo.com/";
    //    String BASE_URL_H5 = "http://www.tata.club/";
//    String BASE_URL_H5 = "https://tatah5.baojiawangluo.com/";


    //推荐列表
    @FormUrlEncoded
    @POST("topic/getFeeds")
    Observable<GetRecommendJson> getRecommend(@Field("name") String name, @Field("offset") String offset, @Field("num") int num);

    //    Observable<GetRecommendJson> getRecommend(@Field("name") String name);
    //此刻列表
    @FormUrlEncoded
    @POST("topic/getLatest")
    Observable<GetNowJson> getNow(@Field("platform") String platform, @Field("offset") String offset, @Field("num") int num);

    //    Observable<GetNowJson> getNow();
    //此刻列表详情
    @FormUrlEncoded
    @POST("topic/getTopicInfo")
    Observable<GetNowDetailsJson> getNow(@Field("tid") int tid);

    //发布帖子
    @FormUrlEncoded
    @POST("topic/add")
    Observable<PublicBean> getAddTopic(@Field("content") String content, @Field("location") String location, @Field("images") String images);

    //新闻详情
    @FormUrlEncoded
    @POST("topic/getNewsInfo")
    Observable<GetNewInfoJson> getNewInfo(@Field("newsid") int newsid);

    //是否关注
    @FormUrlEncoded
    @POST("follow/isFollowed")
    Observable<GetIsFollowJson> getIsFollow(@Field("fids") int fids);

    //是否收藏
    @FormUrlEncoded
    @POST("praise/isPraised")
    Observable<GetIsCollectJson> getIsCollect(@Field("tid") int tid);

    //评论列表
    @FormUrlEncoded
    @POST("reply/getReplies")
    Observable<CommentOnJson> getCommentInfo(@Field("tid") int tid, @Field("type") int type, @Field("direct") int direct);

    //回复评论
    @FormUrlEncoded
    @POST("reply/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> requestComment(@Field("tid") int tid, @Field("pid") int pid, @Field("content") String content, @Field("type") int type);

    //上传图片
    @Multipart
    @POST("image/upload")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<UpdateHeaderFileJson> updateRepairFile(@Part MultipartBody.Part file);

    //发布帖子
    @FormUrlEncoded
    @POST("topic/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> publishTopic(@Field("content") String content, @Field("location") String location, @Field("images") String images, @Field("platform") String platform);

    //发布新闻
    @FormUrlEncoded
    @POST("news/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> publishNews(@Field("title") String title, @Field("cover") String cover, @Field("content") String content, @Field("location") String location);

    //关注
    @FormUrlEncoded
    @POST("follow/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> addFollow(@Field("uid") int uid);

    //取消关注
    @FormUrlEncoded
    @POST("follow/cancel")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> cancelFollow(@Field("uid") int uid);

    //点赞
    @FormUrlEncoded
    @POST("praise/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> addPraises(@Field("tid") int tid, @Field("type") int type);

    //取消点赞
    @FormUrlEncoded
    @POST("praise/cancel")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> cancelPraises(@Field("tid") int tid, @Field("type") int type);

    //收藏
    @FormUrlEncoded
    @POST("favorite/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> addFavorite(@Field("relateid") int newsid);

    //取消收藏
    @FormUrlEncoded
    @POST("favorite/cancel")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> cancelFavorite(@Field("relateid") int newsid);

    //获取用户个人信息
//    @FormUrlEncoded
    @POST("user/getMyUserInfo")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetMyUserInfo> userInfo();

    //修改头像
    @FormUrlEncoded
    @POST("user/setAvatar")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> udateHeaderIcon(@Field("avatar") String avatar);

    //修改昵称
    @FormUrlEncoded
    @POST("user/setNickname")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> udateNickName(@Field("nickname") String nickname);

    //我发布的帖子
    @FormUrlEncoded
    @POST("topic/getUserTopicList")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetNowJson> userPublishList(@Field("uid") int uid, @Field("offset") String offset, @Field("num") int num);

    //我收藏的帖子
    @FormUrlEncoded
    @POST("favorite/getFavoriteList")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetMeCollectJson> userPraiseList(@Field("offset") String offset, @Field("num") int num);

    //删除我发布的帖子
    @FormUrlEncoded
    @POST("topic/delete")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> deleteTopic(@Field("tid") int tid);

    //我关注的人
    @FormUrlEncoded
    @POST("follow/getFollowings")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetCareJson> getCareList(@Field("offset") String offset, @Field("num") int num);
    //我的粉丝
    @FormUrlEncoded
    @POST("follow/getFollowers")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetCareJson> getFansList(@Field("offset") String offset, @Field("num") int num);

    //用户关注的人
    @FormUrlEncoded
    @POST("follow/getUserFollowings")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetCareJson> getUserCareList(@Field("uid") int uid);


    //获取融云token
    @POST("message/getToken")
    Observable<GetRongIMTokenJson> getRongTokenInfo();

    //获取分享信息
    @FormUrlEncoded
    @POST("share/getShareInfo")
    Observable<GetShareInfoJson> getShareInfo(@Field("scene") String scene, @Field("relateid") int relateid, @Field("type") String type);

    //获取我的界面数量信息
    @FormUrlEncoded
    @POST("user/getUserInfo")
    Observable<GetMineCountJson> getMineCountInfo(@Field("uid") int uid);

    //获取我的界面数量信息
    @FormUrlEncoded
    @POST("user/getUserInfos")
    Observable<GetIMUserInfoJson> getIMUserInfo(@Field("uids") String uids);

    //获取banner页面
    @FormUrlEncoded
    @POST("config/getConfig")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<FlushJson> bannerList(@Field("name") String name);

    //获取专题页面
    @FormUrlEncoded
    @POST("config/getConfig")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<EntranceJson> entranceList(@Field("name") String name);

    //获取我发布的新闻列表
    @FormUrlEncoded
    @POST("news/getUserNewsList")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<UserNewsListJson> userNewsList(@Field("uid") int uid, @Field("offset") String offset, @Field("num") int num);

    //删除我发布的单个新闻
    @FormUrlEncoded
    @POST("news/delete")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> deleteNews(@Field("newsid") int newsid);

    //获取自定义标签列表
//    @FormUrlEncoded
    @POST()
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<CustomizeJson> customiseInfo(@Url String url);

    //获取附近地点
    @FormUrlEncoded
    @POST("config/getPlaces")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PlacesInfoJson> placesInfo(@Field("lng") String lng, @Field("lat") String lat);

    //获取省份
//    @FormUrlEncoded
    @POST("config/getProvinces")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<ProvincesListJson> ProvincesListInfo();

    //获取省份所管辖的市
    @FormUrlEncoded
    @POST("config/getCitys")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<CityListJson> CityListInfo(@Field("province") String province);

    //保存个人信息
    @FormUrlEncoded
    @POST("user/setUserInfo")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> setUserInfo(@Field("avatar") String avatar,@Field("nickname") String nickname,@Field("gender") String gender,@Field("city") String city,@Field("signature") String signature);
}
