package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class GetDecorationJson {

    private int errno;
    private String errmsg;
    private DecorationData data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public DecorationData getData() {
        return data;
    }

    public void setData(DecorationData data) {
        this.data = data;
    }

    public class DecorationData {

        private DecorationUser user;
        private DecorationView view;

        public DecorationUser getUser() {
            return user;
        }

        public void setUser(DecorationUser user) {
            this.user = user;
        }

        public DecorationView getView() {
            return view;
        }

        public void setView(DecorationView view) {
            this.view = view;
        }

        public class DecorationUser {

            private int uid;
            private String nickname;
            private String avatar;

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }

        public class DecorationView {

            private DecorationStats stats;
            private ArrayList<DecorationGroup> group;

            public DecorationStats getStats() {
                return stats;
            }

            public void setStats(DecorationStats stats) {
                this.stats = stats;
            }

            public ArrayList<DecorationGroup> getGroup() {
                return group;
            }

            public void setGroup(ArrayList<DecorationGroup> group) {
                this.group = group;
            }

            public class DecorationStats {

                private int total;
                private int ratio;

                public int getTotal() {
                    return total;
                }

                public void setTotal(int total) {
                    this.total = total;
                }

                public int getRatio() {
                    return ratio;
                }

                public void setRatio(int ratio) {
                    this.ratio = ratio;
                }
            }

            public class DecorationGroup {

                private DecorationType type;
                private ArrayList<Decorationlist> list;

                public DecorationType getType() {
                    return type;
                }

                public void setType(DecorationType type) {
                    this.type = type;
                }

                public ArrayList<Decorationlist> getList() {
                    return list;
                }

                public void setList(ArrayList<Decorationlist> list) {
                    this.list = list;
                }

                public class DecorationType {

                    private int id;
                    private String name;

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }

                public class Decorationlist {

                    private int code;
                    private String title;
                    private String image;
                    private String icon;
                    private String content;
                    private boolean upgrade;
                    private String target;
                    private String startime;
                    private String level;
                    private String restrict;
                    private boolean locked;
                    private String addtime;
                    private int progress;

                    public int getCode() {
                        return code;
                    }

                    public void setCode(int code) {
                        this.code = code;
                    }

                    public String getTitle() {
                        return title;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public String getImage() {
                        return image;
                    }

                    public void setImage(String image) {
                        this.image = image;
                    }

                    public String getIcon() {
                        return icon;
                    }

                    public void setIcon(String icon) {
                        this.icon = icon;
                    }

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }

                    public boolean isUpgrade() {
                        return upgrade;
                    }

                    public void setUpgrade(boolean upgrade) {
                        this.upgrade = upgrade;
                    }

                    public String getTarget() {
                        return target;
                    }

                    public void setTarget(String target) {
                        this.target = target;
                    }

                    public String getStartime() {
                        return startime;
                    }

                    public void setStartime(String startime) {
                        this.startime = startime;
                    }

                    public String getLevel() {
                        return level;
                    }

                    public void setLevel(String level) {
                        this.level = level;
                    }

                    public String getRestrict() {
                        return restrict;
                    }

                    public void setRestrict(String restrict) {
                        this.restrict = restrict;
                    }

                    public boolean isLocked() {
                        return locked;
                    }

                    public void setLocked(boolean locked) {
                        this.locked = locked;
                    }

                    public String getAddtime() {
                        return addtime;
                    }

                    public void setAddtime(String addtime) {
                        this.addtime = addtime;
                    }

                    public int getProgress() {
                        return progress;
                    }

                    public void setProgress(int progress) {
                        this.progress = progress;
                    }
                }
            }
        }
    }
}
