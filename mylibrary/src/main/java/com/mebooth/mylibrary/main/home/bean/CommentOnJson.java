package com.mebooth.mylibrary.main.home.bean;

import java.util.ArrayList;

public class CommentOnJson {

    private int errno;
    private String errmsg;
    private CommentData data;

    public CommentOnJson() {
    }

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

    public CommentData getData() {
        return data;
    }

    public void setData(CommentData data) {
        this.data = data;
    }

    public class CommentData {

        private ArrayList<CommentOnList> list;
        private int offset;

        public CommentData() {
        }

        public ArrayList<CommentOnList> getList() {
            return list;
        }

        public void setList(ArrayList<CommentOnList> list) {
            this.list = list;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public class CommentOnList {

            private Reply reply;
            private CommentUser user;

            public CommentOnList() {
            }

            public Reply getReply() {
                return reply;
            }

            public void setReply(Reply reply) {
                this.reply = reply;
            }

            public CommentUser getUser() {
                return user;
            }

            public void setUser(CommentUser user) {
                this.user = user;
            }

            public class Reply {
                private int rid;
                private int uid;
                private int tid;
                private int pid;
                private String content;
                private String addtime;

                private ArrayList<Replies> replies;

                public Reply() {
                }

                public int getRid() {
                    return rid;
                }

                public void setRid(int rid) {
                    this.rid = rid;
                }

                public int getUid() {
                    return uid;
                }

                public void setUid(int uid) {
                    this.uid = uid;
                }

                public int getTid() {
                    return tid;
                }

                public void setTid(int tid) {
                    this.tid = tid;
                }

                public int getPid() {
                    return pid;
                }

                public void setPid(int pid) {
                    this.pid = pid;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getAddtime() {
                    return addtime;
                }

                public void setAddtime(String addtime) {
                    this.addtime = addtime;
                }

                public ArrayList<Replies> getReplies() {
                    return replies;
                }

                public void setReplies(ArrayList<Replies> replies) {
                    this.replies = replies;
                }

                public class Replies {

                    private RepliesReply reply;
                    private RepliesUser user;

                    public Replies() {
                    }

                    public RepliesReply getReply() {
                        return reply;
                    }

                    public void setReply(RepliesReply reply) {
                        this.reply = reply;
                    }

                    public RepliesUser getUser() {
                        return user;
                    }

                    public void setUser(RepliesUser user) {
                        this.user = user;
                    }

                    public class RepliesReply {

                        private int rid;
                        private int uid;
                        private int tid;
                        private int pid;
                        private String content;
                        private String addtime;

                        public int getRid() {
                            return rid;
                        }

                        public void setRid(int rid) {
                            this.rid = rid;
                        }

                        public int getUid() {
                            return uid;
                        }

                        public void setUid(int uid) {
                            this.uid = uid;
                        }

                        public int getTid() {
                            return tid;
                        }

                        public void setTid(int tid) {
                            this.tid = tid;
                        }

                        public int getPid() {
                            return pid;
                        }

                        public void setPid(int pid) {
                            this.pid = pid;
                        }

                        public String getContent() {
                            return content;
                        }

                        public void setContent(String content) {
                            this.content = content;
                        }

                        public String getAddtime() {
                            return addtime;
                        }

                        public void setAddtime(String addtime) {
                            this.addtime = addtime;
                        }
                    }

                    public class RepliesUser {

                        private int uid;
                        private int pid;
                        private String nickname;
                        private String avatar;
                        private String mobile;
                        private String forzen;
                        private String addtime;

                        public int getUid() {
                            return uid;
                        }

                        public void setUid(int uid) {
                            this.uid = uid;
                        }

                        public int getPid() {
                            return pid;
                        }

                        public void setPid(int pid) {
                            this.pid = pid;
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

                        public String getMobile() {
                            return mobile;
                        }

                        public void setMobile(String mobile) {
                            this.mobile = mobile;
                        }

                        public String getForzen() {
                            return forzen;
                        }

                        public void setForzen(String forzen) {
                            this.forzen = forzen;
                        }

                        public String getAddtime() {
                            return addtime;
                        }

                        public void setAddtime(String addtime) {
                            this.addtime = addtime;
                        }
                    }
                }
            }

            public class CommentUser {

                private int uid;
                private int pid;
                private String nickname;
                private String avatar;
                private String mobile;
                private String addtime;

                public int getUid() {
                    return uid;
                }

                public void setUid(int uid) {
                    this.uid = uid;
                }

                public int getPid() {
                    return pid;
                }

                public void setPid(int pid) {
                    this.pid = pid;
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

                public String getMobile() {
                    return mobile;
                }

                public void setMobile(String mobile) {
                    this.mobile = mobile;
                }

                public String getAddtime() {
                    return addtime;
                }

                public void setAddtime(String addtime) {
                    this.addtime = addtime;
                }
            }
        }
    }
}
