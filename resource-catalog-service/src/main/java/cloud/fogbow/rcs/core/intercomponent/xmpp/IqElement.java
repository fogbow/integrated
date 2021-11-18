package cloud.fogbow.rcs.core.intercomponent.xmpp;

public enum IqElement {

    QUERY("query"), CONTENT("content"), MEMBER("member"), SERVICE("service");
    
    private final String element;

    IqElement(final String elementName) {
        this.element = elementName;
    }

    @Override
    public String toString() {
        return element;
    }
}
