  screenFuc();
  var response={};
  var moreResponse={};
  var moreQuestions="";
  var uploadAlready=[];
    function screenFuc() {
        var topHeight = $(".chatBox-head").innerHeight();//聊天头部高度
        //屏幕小于768px时候,布局change
        var winWidth = $(window).innerWidth();
        if (winWidth <= 768) {
            var totalHeight = $(window).height(); //页面整体高度
            $(".chatBox-info").css("height", totalHeight - topHeight);
            var infoHeight = $(".chatBox-info").innerHeight();//聊天头部以下高度
            //中间内容高度
            $(".chatBox-content").css("height", infoHeight - 46);
            $(".chatBox-content-demo").css("height", infoHeight - 46);

            $(".chatBox-list").css("height", totalHeight - topHeight);
            $(".chatBox-kuang").css("height", totalHeight - topHeight);
            $(".div-textarea").css("width", winWidth - 106);
        } else {
            $(".chatBox-info").css("height", 495);
            $(".chatBox-content").css("height", 448);
            $(".chatBox-content-demo").css("height", 448);
            $(".chatBox-list").css("height", 495);
            $(".chatBox-kuang").css("height", 495);
            $(".div-textarea").css("width", 260);
        }
    }
    (window.onresize = function () {
        screenFuc();
    })();
    //未读信息数量为空时
    var totalNum = $(".chat-message-num").html();
    if (totalNum == "") {
        $(".chat-message-num").css("padding", 0);
    }
    $(".message-num").each(function () {
        var wdNum = $(this).html();
        if (wdNum == "") {
            $(this).css("padding", 0);
        }
    });


    //打开/关闭聊天框
    $(".chatBtn").click(function () {
        $(".chatBox").toggle(10);
    })
    $(".chat-close").click(function () {
        $(".chatBox").toggle(10);
    })
    //进聊天页面
    $(".chat-list-people").each(function () {
        $(this).click(function () {
            var n = $(this).index();
            $(".chatBox-head-one").toggle();
            $(".chatBox-head-two").toggle();
            $(".chatBox-list").fadeToggle();
            $(".chatBox-kuang").fadeToggle();

            //传名字
            $(".ChatInfoName").text($(this).children(".chat-name").children("p").eq(0).html());

            //传头像
            $(".ChatInfoHead>img").attr("src", $(this).children().eq(0).children("img").attr("src"));

            //聊天框默认最底部
            $(document).ready(function () {
                $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
            });
        })
    });

    //返回列表
    $(".chat-return").click(function () {
        $(".chatBox-head-one").toggle(1);
        $(".chatBox-head-two").toggle(1);
        $(".chatBox-list").fadeToggle(1);
        $(".chatBox-kuang").fadeToggle(1);
    });

    //      发送信息
    $("#chat-fasong").click(function () {
        var textContent = $(".div-textarea").html().replace(/[\n\r]/g, '<br>')
        if (textContent != "") {
            sendMsg(textContent);
            recvMsg(textContent);
        }
    });

    //      发送表情
    $("#chat-biaoqing").click(function () {
        $(".biaoqing-photo").toggle();
    });
    $(document).click(function () {
        $(".biaoqing-photo").css("display", "none");
    });
    $("#chat-biaoqing").click(function (event) {
        event.stopPropagation();//阻止事件
    });

    //发送表情
    $(".emoji-picker-image").each(function () {
        $(this).click(function () {
            var bq = $(this).parent().html();
            console.log(bq)
            $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
                "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
                "<div class=\"right\"> <div class=\"chat-message\"> " + bq + " </div> " +
                "<div class=\"chat-avatars\"><img src=\"img/icon01.png\" alt=\"头像\" /></div> </div> </div>");
            //发送后关闭表情框
            $(".biaoqing-photo").toggle();
            //聊天框默认最底部
            $(document).ready(function () {
                $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
            });
        })
    });

    //      发送图片
    function selectImg(pic) {
        if (!pic.files || !pic.files[0]) {
            return;
        }
        var reader = new FileReader();
        reader.onload = function (evt) {
            var images = evt.target.result;
            $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
                "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
                "<div class=\"right\"> <div class=\"chat-message\"><img src=" + images + "></div> " +
                "<div class=\"chat-avatars\"><img src=\"img/icon01.png\" alt=\"头像\" /></div> </div> </div>");
            //聊天框默认最底部
            $(document).ready(function () {
                $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
            });
        };
        reader.readAsDataURL(pic.files[0]);

    }

    //发送消息
    function sendMsg(textContent){
        $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
            "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
            "<div class=\"right\"> <div class=\"chat-message\"> " + textContent + " </div> " +
            "<div class=\"chat-avatars\"><img src=\"img/icon02.png\" alt=\"头像\" /></div> </div> </div>");
        //发送后清空输入框
        $(".div-textarea").html("");
        //聊天框默认最底部
        $(document).ready(function () {
            $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
        });
    }

    //接受消息返回
    function recvMsg(textContent){
        $.ajax({
            type:"GET",
            url:"/ajax/getHits?question="+textContent,
            success:function (DATA) {
                response = eval("("+DATA+")");
                console.log(response);
                if(response.hits.total>0){
                    showRecvQuestions();
                }else{
                    showRecvMsg("抱歉我还在学习中，您提的问题我还不知道，在给我一点点时间，我一定学会...");
                }
            },
            error:function () {
                showRecvMsg("后台查询出错啦！");
            }
        });
    }


    //显示查询到的全部问题
    function showRecvQuestions(){
        var questions="为您找到以下相近的问题，请点击查看:<br>";
        $.each(response.hits.hits,function (i,item) {
            questions += (i+1)+".<a href='javascript:void(0);' style=\"text-decoration : none \" onclick='getAnswer(\""+ item._id+"\")'>" +  item._source.question +"</a><br>"
        })
        $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
            "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
            "<div class=\"left\"><div class=\"chat-avatars\"><img src=\"img/icon01.png\" alt=\"头像\" /> </div> " +
            "<div class=\"chat-message\"> " + questions +"</div> </div> </div>");
        //聊天框默认最底部
        $(document).ready(function () {
            $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
        });
    }

    //根据问题id 后台查询问题详情
    function getAnswer(id) {
        $.ajax({
            type:"GET",
            url:"/ajax/getAnswer?id="+id,
            success:function (DATA) {
                var data = eval("("+DATA+")");
                getMoreQuestion(data.indistinction,id);
                sendMsg(data.question);
                showRecvMsgWithCustomer(data,id);

            },
            error:function () {
                showRecvMsg("您当前网络不佳哦，请稍后再试~");
            }
        });
    }

    //格式化获取当前时间戳yyyy-MM-dd HH:mm:SS
    function getFormatDate(){
      var nowDate = new Date();
          var year = nowDate.getFullYear();
          var month = nowDate.getMonth() + 1 < 10 ? "0" + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1;
          var date = nowDate.getDate() < 10 ? "0" + nowDate.getDate() : nowDate.getDate();
          var hour = nowDate.getHours()< 10 ? "0" + nowDate.getHours() : nowDate.getHours();
          var minute = nowDate.getMinutes()< 10 ? "0" + nowDate.getMinutes() : nowDate.getMinutes();
          var second = nowDate.getSeconds()< 10 ? "0" + nowDate.getSeconds() : nowDate.getSeconds();
          return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
    }
    //显示回答
    function showRecvMsg(textContent) {
      $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
          "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
          "<div class=\"left\"><div class=\"chat-avatars\"><img src=\"img/icon01.png\" alt=\"头像\" /> </div> " +
          "<div class=\"chat-message\"> " + textContent +"</div> </div> </div>");
      //聊天框默认最底部
      $(document).ready(function () {
          $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
      });
  }

    //显示人工客户回答及有帮助提示提示
    function showRecvMsgWithCustomer(data,id){
        var textContent="<div class=\"opera\"><br>是否对您有帮助呢？<br>" +
            "<span id=\"btn\" onclick='getAdmireClick("+id+","+data.like+")'>" +
            "<i class=\"iconfont\">&#xe717;</i> 有帮助" +
            "</span>" +
            "<span style='padding: 8px 12px;' id=\"btn\" onclick='getNotAdmireClick("+id+","+data.dislike+")'>" +
            "<i class=\"iconfont\">&#xe716;</i> 没帮助" +
            "</span>" +
            "</div>";
        $(".chatBox-content-demo").append("<div class=\"clearfloat\">" +
            "<div class=\"author-name\"><small class=\"chat-date\">"+getFormatDate()+"</small> </div> " +
            "<div class=\"left\"><div class=\"chat-avatars\"><img src=\"img/icon01.png\" alt=\"头像\" /> </div> " +
            "<div style='display: inline-block;'><div class=\"chat-message\" style='    border-bottom-left-radius: 0px;" +
            "    border-bottom-right-radius: 0px;'> " + (data.answer+moreQuestions+textContent) +"</div><a href=\"javascript:void(0)\" style=\"padding: 0 24px; height: 30px;line-height: 30px;font-size=15px;font-size: 15px;border-top-left-radius:  0px;border-top-right-radius:  0px;\" onclick=\"getCustomeService()\" class=\"button button-block button-rounded button-primary button-large\">人工服务(6:00~23:00)</a>" +
            " </div></div> </div>");
        //聊天框默认最底部
        $(document).ready(function () {
            $("#chatBox-content-demo").scrollTop($("#chatBox-content-demo")[0].scrollHeight);
        });
    }
    //获取“你或许还想了解”
    function getMoreQuestion(indistinction,id){
        $.ajax({
            type:"GET",
            url:"/ajax/getMoreQuestions?indistinction="+indistinction+"&id="+id,
            async: false,
            success:function (DATA) {
                moreResponse = eval("("+DATA+")");
                console.log(moreResponse);
                if(moreResponse.hits.total>0){
                    moreQuestions="<br><br>小招猜您还想了解以下内容:<br>";
                    $.each(moreResponse.hits.hits,function (i,item) {
                        moreQuestions += (i+1)+".<a href='javascript:void(0);' style=\"text-decoration : none \" onclick='getAnswer(\""+ item._id+"\")'>" +  item._source.question +"</a><br>"
                    })
                }else{
                    moreQuestions="";
                }
            },
            error:function () {
                showRecvMsg("您当前网络不佳哦，请稍后再试~");
            }
        });
    }

    //人工服务接口
    function getCustomeService(){
        sendMsg("人工服务");
    }

    //上传点赞
    function getAdmireClick(id,like) {
        var isUploaded = false;
        $.each(uploadAlready, function (key, val) {
            if (val === id) {
                isUploaded = true;
                showRecvMsg("这个问题您已经反馈过了哦，请不要重复反馈^_^");
                return;
            }
        });
        if (!isUploaded) {
            uploadAlready.push(id);
            $.ajax({
                type: "GET",
                url: "/ajax/upLoadLike?id=" + id + "&like=" + like,
                success: function (DATA) {
                    console.log(DATA)
                },
                error: function () {
                    showRecvMsg("您当前网络不佳哦，请稍后再试~");
                }
            });
            showRecvMsg("很开心能够帮到您，若有需要可继续提问哦^_^");
        }
    }
    //上传没帮助
    function getNotAdmireClick(id,dislike){
        var isUploaded = false;
        $.each(uploadAlready, function (key, val) {
            if (val === id) {
                isUploaded = true;
                showRecvMsg("这个问题您已经反馈过了哦，请不要重复反馈^_^");
                return;
            }
        });
        if (!isUploaded) {
            uploadAlready.push(id);
            showRecvMsg("非常抱歉没能找到您要的答案，关于这个问题，请联系财务在线项目组：xxxxx或邮件反馈至财务在线项目组邮箱xxxx");
        }
    }


