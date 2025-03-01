package com.chihihx.launcher.bean;

public class MoviceList {
    private MoviceData[] Youtube;
    private Inner Others;

    public MoviceData[] getYoutube() {
        return Youtube;
    }
    public Inner getOthers() {
        return Others;
    }

    public class Inner{
        private MoviceData[] Netflix;
        private MoviceData[] Disney;
        private MoviceData[] Prime_video;
        private MoviceData[] Max;
        private MoviceData[] Hulu;

        public MoviceData[] getDisney() {
            return Disney;
        }

        public MoviceData[] getHulu() {
            return Hulu;
        }

        public MoviceData[] getMax() {
            return Max;
        }

        public MoviceData[] getNetflix() {
            return Netflix;
        }

        public MoviceData[] getPrime_video() {
            return Prime_video;
        }
    }
}
