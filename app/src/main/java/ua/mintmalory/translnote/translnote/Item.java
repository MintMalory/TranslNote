package ua.mintmalory.translnote.translnote;

import java.util.Date;
import java.util.UUID;

public class Item {
    private String header;
    private String text;
    private final Date creationDate;
    private UUID mId;

    public Item(String header, String text, Date creationDate) {
        if (header == null) {
            header = "";
        }

        if (text == null) {
            text = "";
        }

        if (creationDate == null) {
            throw new IllegalArgumentException("Date of creation can't be null");
        }

        this.header = header;
        this.text = text;
        mId = UUID.randomUUID();
        this.creationDate = creationDate;
    }

    public void setHeader(String newHeader) {
        header = newHeader;
    }

    public void setText(String newText) {
        text = newText;
    }

    public String getText() {
        return text;
    }

    public String getHeader() {
        return header;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public UUID getId() {
        return mId;
    }
}
