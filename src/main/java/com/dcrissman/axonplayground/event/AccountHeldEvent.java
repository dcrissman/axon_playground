package com.dcrissman.axonplayground.event;

import com.dcrissman.axonplayground.BaseEvent;
import com.dcrissman.axonplayground.Status;

public class AccountHeldEvent extends BaseEvent<String> {

    public final Status status;

    public AccountHeldEvent(String id, Status status) {
        super(id);
        this.status = status;
    }

}
