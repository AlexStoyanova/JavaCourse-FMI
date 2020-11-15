package bg.sofia.uni.fmi.mjt.warehouse;

import java.time.LocalDateTime;

public record Pair<P>(P parcel, LocalDateTime submissionDate) {
}
