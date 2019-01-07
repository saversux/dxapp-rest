/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxapp.rest.cmd.responses;

import com.google.gson.annotations.SerializedName;

/**
 * @author Julien Bernhart, 2019-01-07
 */
public class ApplicationProcessResponse {
    @SerializedName("name")
    private String m_name;
    @SerializedName("id")
    private int m_id;
    @SerializedName("elapsedTime")
    private long m_elapsedTime;
    @SerializedName("arguments")
    private String m_arguments;

    public ApplicationProcessResponse(String p_name, int p_id, long p_elapsedTime, String p_arguments) {
        m_arguments = p_arguments;
        m_elapsedTime = p_elapsedTime;
        m_id = p_id;
        m_name = p_name;
    }
}
