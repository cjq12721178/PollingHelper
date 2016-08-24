package com.example.kat.pollinghelper.structure.scout;

/**
 * Created by KAT on 2016/5/24.
 */
public class ScoutCellClause {
    public ScoutCellClause(String name, Object content) {
        this.label = name;
        this.content = content;
        modified = false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getContent() {
        return content;
    }

    public String getContentString() {
        return content != null ? content.toString() : "";
    }

    public void setContent(Object content) {
        if (content == null) {
            if (this.content == null)
                return;
        } else if (content.equals(this.content))
            return;

        this.content = content;
        modified = true;
//        if ((this.content == null && content != null) ||
//                !this.content.equals(content)) {
//            this.content = content;
//            modified = true;
//        }
    }

    public boolean isModified() {
        return modified;
    }

    public boolean contentStringToDouble() {
        boolean result = false;
        if (content instanceof Double) {
            result = true;
        } else {
            try {
                content = Double.valueOf((String)content);
                result = true;
            } catch (NumberFormatException numberFormatException) {
            } catch (Exception elseException) {
            }
        }
        return result;
    }

    private String label;
    private Object content;
    private boolean modified;
}
