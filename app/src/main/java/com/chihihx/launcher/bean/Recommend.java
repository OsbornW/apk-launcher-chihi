package com.chihihx.launcher.bean;

public class Recommend {
    private String id;
    private Inner snippet;

    public String getId() {
        return id;
    }

    public Inner getSnippet() {
        return snippet;
    }

    public class Inner{
        private String publishedAt;
        private String channelId;
        private String title;
        private String description;
        private ThumbaailsHub thumbnails;
        private String[] tags;

        public String getTitle() {
            return title;
        }

        public String getChannelId() {
            return channelId;
        }

        public String getDescription() {
            return description;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public String[] getTags() {
            return tags;
        }

        public ThumbaailsHub getThumbnails() {
            return thumbnails;
        }
    }

    public class ThumbaailsHub{
        private Thumbaails standard;
        private Thumbaails maxres;

        public Thumbaails getMaxres() {
            return maxres;
        }

        public Thumbaails getStandard() {
            return standard;
        }
    }

    public class Thumbaails{
        private String url;
        private int width;
        private int height;

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
