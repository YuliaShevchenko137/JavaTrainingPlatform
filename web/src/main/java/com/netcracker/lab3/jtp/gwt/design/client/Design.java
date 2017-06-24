package com.netcracker.lab3.jtp.gwt.design.client;


import com.google.gwt.user.client.ui.*;

public class Design {

    public static Panel createParagraph(String text) {
        return new HTMLPanel("p", text);
    }

    private static Widget getHeader(){
        HTMLPanel header = new HTMLPanel("header", "");
        Panel paragraph = createParagraph("Java Training Platform");
        header.add(paragraph);
        return header;
    }

    private static Widget getFooter(){
        HTMLPanel footer = new HTMLPanel("footer", "");
        Anchor footerAnchor = new Anchor();
        footerAnchor.setHref("https://github.com/YuliaShevchenko137/JavaTrainingPlatform");
        footerAnchor.setText("Source code");
        footer.add(footerAnchor);
        return footer;
    }

    private static Widget getMenu(){
        HTMLPanel menu = new HTMLPanel("nav", "");
        menu.add(createParagraph("Menu"));
        return menu;
    }

    public static Panel getContent() {
        return new HTMLPanel("section", "");
    }

    public static void fillBody(Panel content, RootPanel rootPanel){
        rootPanel.add(Design.getHeader());
        rootPanel.add(Design.getMenu());
        rootPanel.add(content);
        rootPanel.add(Design.getFooter());
    }

}
