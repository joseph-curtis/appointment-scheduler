/*
 Copyright 2022 Joseph Curtis Licensed under the Educational
 Community License, Version 2.0 (the "License"); you may not use this file
 except in compliance with the License. You may obtain a copy of the License at

 http://opensource.org/licenses/ECL-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.

 ******************************************************************************/

package DAO;

import javafx.collections.ObservableList;
import model.DataTransferObject;

import java.util.Optional;

/**
 * Interface for a Data Access Object that cannot change (create, update, delete).
 * This DAO can read from the database only.
 * @author Joseph Curtis
 * @version 2022.07.23
 */
public interface ImmutableDAO <T extends DataTransferObject> {

    /**
     * @return all the DTO records as an observable list.
     * @throws Exception if any error occurs.
     */
    ObservableList<T> getAll() throws Exception;

    /**
     * @param id unique identifier of the DTO record.
     * @return an optional container with a DTO record.
     * if one with id exists, empty optional otherwise.
     * @throws Exception if any error occurs.
     */
    Optional<T> getById(int id) throws Exception;
}
