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
import utility.DBUtil;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object that interacts with Data Transfer Objects.
 * @author Joseph Curtis
 * @version 2022.05.19
 */
public abstract class DataAccessObject <T extends DataTransferObject> {

    protected static final Logger log = Logger.getLogger("log.txt");
    protected final DataSource dataSource;

    /**
     * Constructor for DAO with provided <code>dataSource</code> object from {@link DBUtil}.
     * Datasource is used to get connections to database.
     * <p>Use <code>getConnection()</code> method within a try-with-resources block.</p>
     */
    public DataAccessObject() {
        dataSource = DBUtil.getDataSource();
    }

    /**
     * @return all the DTO records as an observable list.
     * @throws Exception if any error occurs.
     */
    abstract ObservableList<T> getAll() throws Exception;

    /**
     * @param id unique identifier of the DTO record.
     * @return an optional container with a DTO record.
     * if one with id exists, empty optional otherwise.
     * @throws Exception if any error occurs.
     */
    abstract Optional<T> getById(int id) throws Exception;

    /**
     * @param dto the DTO record to be added.
     * @return true if DTO record was added, false if record already exists.
     * @throws Exception if any error occurs.
     */
    abstract boolean add(T dto) throws Exception;

    /**
     * @param dto the DTO record to be updated.
     * @return true if DTO record exists and is updated, else false.
     * @throws Exception if any error occurs.
     */
    abstract boolean update(T dto) throws Exception;

    /**
     * @param id the ID of the record to be deleted.
     * @return true if record exists and is deleted, false otherwise.
     * @throws Exception if any error occurs.
     */
    abstract boolean delete(int id) throws Exception;

    /**
     * Create a DTO from SQL result set.
     * @param resultSet object from SQL query
     * @return the Data Transfer Object
     * @throws Exception
     */
    protected abstract T createDtoRecord(ResultSet resultSet) throws Exception;
}
