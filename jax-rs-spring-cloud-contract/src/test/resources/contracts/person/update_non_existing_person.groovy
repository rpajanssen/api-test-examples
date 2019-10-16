package contracts.person.wired

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should not update a person that does not exist"

    request {
        url "/person"
        method PUT()
        headers {
            contentType applicationJson()
        }
        body (
                id: 25,
                firstName: "Johnie",
                lastName: "Hacker"
        )
    }

    // note that we call the contains-method implemented in the base test class - we were unable (to dumb) to get the
    // regex to work properly
    response {
        status NOT_FOUND()
        headers {
            contentType applicationJson()
        }
        body([
                code: 'NOT_FOUND',
                message : $(producer(execute('contains($it, "person does not exist")')))
        ])
    }
}
