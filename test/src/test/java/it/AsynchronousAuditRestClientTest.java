package it;

import com.atlassian.jira.nimblefunctests.annotation.Restore;
import com.atlassian.jira.rest.client.api.domain.AuditAssociatedItem;
import com.atlassian.jira.rest.client.api.domain.AuditChangedValue;
import com.atlassian.jira.rest.client.api.domain.AuditRecord;
import com.atlassian.jira.rest.client.api.domain.Component;
import com.atlassian.jira.rest.client.api.domain.input.AuditRecordSearchInput;
import com.atlassian.jira.rest.client.api.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.internal.json.TestConstants;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Test;

import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@Restore(TestConstants.DEFAULT_JIRA_DUMP_FILE)
public class AsynchronousAuditRestClientTest  extends AbstractAsynchronousRestClientTest {

    @Test
    public void testGetRecords() {

        final Component component = client.getComponentClient().createComponent("TST", new ComponentInput("New TST Component", null, null, null)).claim();
        assertNotNull(component);

        final Iterable<AuditRecord> auditRecords = client.getAuditRestClient().getAuditRecords(new AuditRecordSearchInput(null, null, null, null, null)).claim();
        final Iterable<AuditRecord> filterResult = Iterables.filter(auditRecords, new Predicate<AuditRecord>() {
            @Override
            public boolean apply(final AuditRecord input) {
                if (input.getSummary().equals("Project component created") &&
                        input.getObjectItem().getName().equals("New TST Component")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        final Iterator<AuditRecord> iterator = filterResult.iterator();
        assertThat(iterator.hasNext(), is(true));
        final AuditRecord record = iterator.next();
        assertThat(record.getAuthorKey(), is("admin"));
        assertThat(record.getObjectItem().getTypeName(), is("PROJECT_COMPONENT"));

        final Iterator<AuditAssociatedItem> itemIterator = record.getAssociatedItem().iterator();
        final AuditAssociatedItem item1 = itemIterator.next();
        assertThat(item1.getName(), is("Test Project"));
        assertThat(item1.getTypeName(), is("PROJECT"));

        final AuditAssociatedItem item2 = itemIterator.next();
        assertThat(item2.getName(), is("admin"));
        assertThat(item2.getTypeName(), is("USER"));
        assertThat(item2.getParentId(), is("1"));
        assertThat(item2.getParentName(), is("JIRA Internal Directory"));

        final Iterator<AuditChangedValue> valuesIterator = record.getChangedValues().iterator();
        final AuditChangedValue value1 = valuesIterator.next();
        assertThat(value1.getFieldName(), is("Name"));
        assertThat(value1.getChangedTo(), is("New TST Component"));

         final AuditChangedValue value2 = valuesIterator.next();
        assertThat(value2.getFieldName(), is("Default Assignee"));
        assertThat(value2.getChangedTo(), is("Project Default"));
    }

    @Test
    public void testGetRecordsWithParams() {
        final Iterable<AuditRecord> auditRecords = client.getAuditRestClient().getAuditRecords(new AuditRecordSearchInput(1, null, null, null, null)).claim();
        assertThat(Iterables.size(auditRecords), is(2));
    }
}
