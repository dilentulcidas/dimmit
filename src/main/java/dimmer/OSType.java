package dimmer;

/**
 * DimmerManager initializer class. The manager will load all the available monitors connected to the system
 * at first launch.
 */
public enum OSType
{
    Windows {
        @Override
        public DimmerManager getDimmerRunner()
        {
            return new DimmerForWindows();
        }
    },
    MacOS {
        @Override
        public DimmerManager getDimmerRunner()
        {
            // todo: not implemented
            return new DimmerForMacOS();
        }
    },
    Linux {
        @Override
        public DimmerManager getDimmerRunner()
        {
            // todo: not implemented
            return new DimmerForLinux();
        }
    };

    public abstract DimmerManager getDimmerRunner();
}
