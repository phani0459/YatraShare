package com.yatrashare.dtos;

import java.util.ArrayList;

/**
 * Created by KANDAGATLAS on 10-01-2016.
 */
public class MoreDataDTO {

    public String ContentEncoding;
    public String ContentType;
    public ArrayList<MoreSection> Data;
    public String JsonRequestBehavior;
    public String MaxJsonLength;
    public String RecursionLimit;

    public class MoreSection {
        public String PageContentId;
        public String PageId;
        public String PageName;
        public String SectionNo;
        public String SectionName;
        public String Content;
    }

}
