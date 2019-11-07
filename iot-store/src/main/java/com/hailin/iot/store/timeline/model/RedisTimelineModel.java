package com.hailin.iot.store.timeline.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RedisTimelineModel implements TimeLineModel  {

    private byte[] key;

    private byte[] timeLineId;

}
