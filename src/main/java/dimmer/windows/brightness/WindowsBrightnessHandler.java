package dimmer.windows.brightness;

public interface WindowsBrightnessHandler
{
    String getCurrentBrightness();
    void setBrightness(int brightnessNumber) throws Exception;
}
