package runner;

public class DimmerForLinux implements DimmerRunner
{
    @Override
    public void run()
    {
        throw new IllegalStateException("Operating system not supported!");
    }
}
