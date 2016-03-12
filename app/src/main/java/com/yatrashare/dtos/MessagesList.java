package com.yatrashare.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 04-01-2016.
 */
public class MessagesList implements Serializable{
    public String ContentEncoding;
    public String ContentType;
    public ArrayList<MessagesListData> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class MessagesListData implements Serializable{
        public String MessageId;
        public String SenderId;
        public String ReceiverId;
        public String MessageGuid;
        public String SenderGuid;
        public String ReceiverGuid;
        public String MessageRead;
        public String Message;
        public String PossibleRideGuid;
        public String MessageStatus;
        public String SenderProfilePic;
        public String SenderName;
        public String Route;
        public String RideDate;
        public String MessageReceivedTime;
        public String MessageSentTime;
        public String Name;
        public String ProfilePic;
        public String TypeOfMessage;
        public String TotalInboxMessages;
        public String TotalArchivedMessages;
        public String IsArchived;
    }
}
