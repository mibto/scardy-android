package me.scardy;

public enum Permission {
    FULL_NAME, ADDRESS, PHONE, EMAIL;


    private String value;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

}
