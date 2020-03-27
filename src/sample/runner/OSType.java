package sample.runner;

public enum OSType
{
    Windows {
        @Override
        public DimmerRunner getDimmerRunner()
        {
            return new DimmerForWindows();
        }
    },
    MacOS {
        @Override
        public DimmerRunner getDimmerRunner()
        {
            // todo: not implemented
            return new DimmerForMacOS();
        }
    },
    Linux {
        @Override
        public DimmerRunner getDimmerRunner()
        {
            // todo: not implemented
            return new DimmerForLinux();
        }
    };

    public abstract DimmerRunner getDimmerRunner();
}
