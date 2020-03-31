package com.lotus.base;

import java.util.List;
import java.util.Map;

/**
 * EventBus发送消息实体类，具体使用在底部
 */
public class MessageEvent {

    public static final String TYPE_ATTACHMENT_UPDATE = "attachment_update";//附件更新
    public static final String TYPE_ATTACHMENT_ADD = "attachment_add";//附件增加
    public static final String TYPE_ATTACHMENT_DELETE = "attachment_delete";//附件删除

    public static final String TYPE_STATISTICS_INFO = "statistics_info";//统计信息

    public static final String TYPE_HOME_MENU_CHANGE = "home_menu_change";//首页菜单改变

    private String type;//消息所属类型

    private String content;//文本信息
    private int position;//位序
    private List<String> stringList;
    private Map<String, String> stringMap;//使用：统计信息

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public MessageEvent(String type){
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

/*
=====================任何地方直接发消息=====================
MessageEvent messageEvent = new MessageEvent(MessageEvent.TYPE_ATTACHMENT_DELETE);
messageEvent.setAttachment(attachment);
messageEvent.setPosition(position);
EventBus.getDefault().post(messageEvent);

=====================接收消息=====================
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  EventBus.getDefault().register(this);//注册
  return mRootView;
}

//表明主线程接收消息
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(MessageEvent messageEvent) {
    if(messageEvent != null){
   }
}

@Override
public void onDestroyView() {
  super.onDestroyView();
  EventBus.getDefault().unregister(this);  //注销
}

*/
