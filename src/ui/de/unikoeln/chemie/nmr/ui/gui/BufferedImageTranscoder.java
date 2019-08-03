package de.unikoeln.chemie.nmr.ui.gui;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * Many thanks to bb-generation for sharing this code!
 * @author bb-generation
 * @link http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage/
 * @link In case the link above is still down: https://web.archive.org/web/20131215231214/http://bbgen.net/blog/2011/06/java-svg-to-bufferedimage/
 */

public class BufferedImageTranscoder extends ImageTranscoder {

    private BufferedImage img = null;

    @Override
    public BufferedImage createImage(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return bi;
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException {
        this.img = img;
    }

    public BufferedImage getBufferedImage() {
        return img;
    }
}