package com.mebooth.mylibrary.main.utils;

import com.mebooth.mylibrary.main.home.bean.CommentOnJson;
import com.mebooth.mylibrary.main.home.bean.GetCareJson;
import com.mebooth.mylibrary.main.home.bean.GetIsCollectJson;
import com.mebooth.mylibrary.main.home.bean.GetIsFollowJson;
import com.mebooth.mylibrary.main.home.bean.GetMyUserInfo;
import com.mebooth.mylibrary.main.home.bean.GetNewInfoJson;
import com.mebooth.mylibrary.main.home.bean.GetNowDetailsJson;
import com.mebooth.mylibrary.main.home.bean.GetNowJson;
import com.mebooth.mylibrary.main.home.bean.GetRecommendJson;
import com.mebooth.mylibrary.main.home.bean.GetRongIMTokenJson;
import com.mebooth.mylibrary.main.home.bean.PublicBean;
import com.mebooth.mylibrary.main.home.bean.UpdateHeaderFileJson;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface YService {
    //测试
//    String BASE_URL = "http://api.tata.club/";
    //外网s
//    String BASE_URL = "http://test.tatabike.com/";
    String BASE_URL = "http://tataclub.baojiawangluo.com/";
    //    String BASE_URL_H5 = "http://www.tata.club/";
//    String BASE_URL_H5 = "https://tatah5.baojiawangluo.com/";

    //推荐列表
    @FormUrlEncoded
    @POST(BASE_URL + "topic/getFeeds")
    Observable<GetRecommendJson> getRecommend(@Field("name") String name, @Field("offset") int offset, @Field("num") int num);
//    Observable<GetRecommendJson> getRecommend(@Field("name") String name);
    //此刻列表
    @FormUrlEncoded
    @POST(BASE_URL + "topic/getLatest")
    Observable<GetNowJson> getNow(@Field("offset") int offset, @Field("num") int num);
//    Observable<GetNowJson> getNow();
    //此刻列表详情
    @FormUrlEncoded
    @POST(BASE_URL + "topic/getTopicInfo")
    Observable<GetNowDetailsJson> getNow(@Field("tid") int tid);
    //发布帖子
    @FormUrlEncoded
    @POST(BASE_URL + "topic/add")
    Observable<PublicBean> getAddTopic(@Field("content") String content, @Field("location") String location, @Field("images") String images);
    //新闻详情
    @FormUrlEncoded
    @POST(BASE_URL + "news/getNewsInfo")
    Observable<GetNewInfoJson> getNewInfo(@Field("newsid") int newsid);
    //是否关注
    @FormUrlEncoded
    @POST(BASE_URL + "follow/isFollowed")
    Observable<GetIsFollowJson> getIsFollow(@Field("fids") int fids);
    //是否收藏
    @FormUrlEncoded
    @POST(BASE_URL + "praise/isPraised")
    Observable<GetIsCollectJson> getIsCollect(@Field("tid") int tid);
    //评论列表
    @FormUrlEncoded
    @POST(BASE_URL + "reply/getReplies")
    Observable<CommentOnJson> getCommentInfo(@Field("tid") int tid, @Field("type") int type, @Field("direct") int direct);
    //回复评论
    @FormUrlEncoded
    @POST(BASE_URL + "reply/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> requestComment(@Field("tid") int tid, @Field("pid") int pid, @Field("content") String content);
    //上传图片
    @Multipart
    @POST(BASE_URL + "image/upload")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<UpdateHeaderFileJson> updateRepairFile(@Part MultipartBody.Part file);
    //回复评论
    @FormUrlEncoded
    @POST(BASE_URL + "topic/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> publishTopic(@Field("content") String content, @Field("location") String location, @Field("images") String images);
    //关注
    @FormUrlEncoded
    @POST(BASE_URL + "follow/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> addFollow(@Field("uid") int uid);
    //取消关注
    @FormUrlEncoded
    @POST(BASE_URL + "follow/cancel")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> cancelFollow(@Field("uid") int uid);
    //收藏
    @FormUrlEncoded
    @POST(BASE_URL + "praise/add")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> addPraises(@Field("tid") int tid);
    //获取用户个人信息
//    @FormUrlEncoded
    @POST(BASE_URL + "user/getMyUserInfo")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetMyUserInfo> userInfo();

    //修改头像
    @FormUrlEncoded
    @POST(BASE_URL + "user/setAvatar")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> udateHeaderIcon(@Field("avatar") String avatar);
    //修改昵称
    @FormUrlEncoded
    @POST(BASE_URL + "user/setNickname")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> udateNickName(@Field("nickname") String nickname);
    //我发布的帖子
    @FormUrlEncoded
    @POST(BASE_URL + "topic/getUserTopicList")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetNowJson> userPublishList(@Field("uid") int uid, @Field("offset") int offset, @Field("num") int num);
    //我收藏的帖子
    @FormUrlEncoded
    @POST(BASE_URL + "praise/getPraiseList")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetNowJson> userPraiseList(@Field("offset") int offset, @Field("num") int num);
    //删除我发布的帖子
    @FormUrlEncoded
    @POST(BASE_URL + "topic/delete")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<PublicBean> deleteTopic(@Field("tid") int tid);
    //我关注的人
//    @FormUrlEncoded
    @POST(BASE_URL + "follow/getFollowings")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetCareJson> getCareList();
    //用户关注的人
    @FormUrlEncoded
    @POST(BASE_URL + "follow/getUserFollowings")
//    Observable<UpdateHeaderFileJson> updateRepairFile(@PartMap Map<String, RequestBody> params);
    Observable<GetCareJson> getUserCareList(@Field("uid") int uid);

    //获取融云token
    @POST(BASE_URL + "message/getToken")
    Observable<GetRongIMTokenJson> getRongTokenInfo();


}
