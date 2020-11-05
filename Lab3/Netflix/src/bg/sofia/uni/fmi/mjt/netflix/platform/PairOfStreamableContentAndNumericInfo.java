package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;

public record PairOfStreamableContentAndNumericInfo(Streamable content, int number) {
    public PairOfStreamableContentAndNumericInfo {
        if (content == null) {
            throw new NullPointerException("The content is null");
        }
    }
}
