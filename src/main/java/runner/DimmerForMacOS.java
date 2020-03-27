package runner;

public class DimmerForMacOS implements DimmerRunner
{
    @Override
    public void run()
    {
        throw new IllegalStateException("Operating system not supported!");
    }
}
