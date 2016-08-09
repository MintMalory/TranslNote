package ua.mintmalory.translnote.translnote.model;

import java.util.Date;
import java.util.UUID;

public class Note {
    private String header;
    private String text;
    private final Date creationDate;
    private UUID mId;

    public Note(String header, String text, Date creationDate) {
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

    public synchronized void setHeader(String newHeader) {
        header = newHeader;
    }

    public synchronized void setText(String newText) {
        text = newText;
    }

    public synchronized String getText() {
        return text;
    }

    public synchronized String getHeader() {
        return header;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public UUID getId() {
        return mId;
    }
}
